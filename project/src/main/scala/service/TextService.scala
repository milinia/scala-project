package ru.itis
package service

import dao.TextDao
import domain.domain.{IOWithRequestContext, TextDate, TextToken}
import domain.errors.{AppError, InternalError}
import domain.{PasteText, RequestContext, Text, errors}
import helpers.TimeConverter

import cats.implicits.catsSyntaxApplicativeError
import cats.syntax.either._
import doobie._
import doobie.implicits._
import tofu.logging.Logging

import java.time.Instant

trait TextService {
  def findByToken(
      token: TextToken
  ): IOWithRequestContext[Either[errors.AppError, Option[Text]]]
  def deleteByToken(
      token: TextToken
  ): IOWithRequestContext[Either[errors.AppError, Unit]]
  def create(
      text: PasteText
  ): IOWithRequestContext[Either[errors.AppError, Text]]
}

object TextService {

  private final case class TextServiceImpl(
      dao: TextDao,
      //тайп класс из doobie, который может выполнять транзакцию(преобразовывает в тип IO)
      transactor: Transactor[IOWithRequestContext]
  ) extends TextService {

    override def findByToken(
        token: TextToken
    ): IOWithRequestContext[Either[errors.AppError, Option[Text]]] =
      dao
        .findByToken(token)
        .transact(transactor)
        .attempt
        .map(_.leftMap(errors.InternalError))

    override def deleteByToken(
        token: TextToken
    ): IOWithRequestContext[Either[AppError, Unit]] =
      dao.deleteByToken(token).transact(transactor).attempt.map {
        case Left(th)           => InternalError(th).asLeft[Unit]
        case Right(Left(error)) => error.asLeft[Unit]
        case _                  => ().asRight[AppError]
      }

    override def create(
        text: PasteText
    ): IOWithRequestContext[Either[AppError, Text]] = {
      val now = Instant.now()
      dao
        .createText(
          text,
          TextDate(now),
          TextDate(
            TimeConverter.addExpirationTimeToDate(now, text.expirationTime)
          )
        )
        .transact(transactor)
        .attempt
        .map {
          case Left(th)           => InternalError(th).asLeft[Text]
          case Right(Left(error)) => error.asLeft[Text]
          case Right(Right(text)) => text.asRight[AppError]
        }
    }
  }

  private final class LoggingTextServiceImpl(service: TextService)(implicit
      logging: Logging[IOWithRequestContext]
  ) extends TextService {

    override def findByToken(
        token: TextToken
    ): IOWithRequestContext[Either[AppError, Option[Text]]] =
      for {
        _ <- logging.info("")
        res <- service.findByToken(token)
        _ <- res match {
          case Left(error)  => logging.error(s"")
          case Right(value) => logging.info("")
        }
      } yield res

    override def deleteByToken(
        token: TextToken
    ): IOWithRequestContext[Either[AppError, Unit]] =
      service.deleteByToken(token)

    override def create(
        text: PasteText
    ): IOWithRequestContext[Either[AppError, Text]] =
      service.create(text)
  }

  def make(
      dao: TextDao,
      transactor: Transactor[IOWithRequestContext]
  ): TextService = {
    implicit val logs =
      Logging.Make
        .contextual[IOWithRequestContext, RequestContext]
        .forService[TextService]
    val service = TextServiceImpl(dao, transactor)
    new LoggingTextServiceImpl(service)
  }
}
