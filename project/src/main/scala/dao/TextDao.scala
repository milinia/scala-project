package ru.itis
package dao

import domain.domain.{TextDate, TextId}
import domain.errors.TextNotFound
import domain.{PasteText, Text, errors}
import helpers.TimeConverter

import cats.syntax.either._
import doobie._
import doobie.implicits._

import java.time.Instant

trait TextDao {
  def findById(id: TextId): ConnectionIO[Option[Text]]
  def deleteById(id: TextId): ConnectionIO[Either[TextNotFound, Unit]]
  def createText(
      text: PasteText,
      createdAt: TextDate,
      expirationTime: TextDate
  ): ConnectionIO[Either[errors.InternalError, Text]]
}

object TextDao {
  object sql {
    def findByIdSql(id: TextId): Query0[Text] = {
      sql"select * from TEXT where token=${id.value}"
        .query[Text] //query прочитывает ответный тип данных
    }
    def deleteByIdSql(id: TextId): Update0 = {
      sql"delete from TEXT where token=${id.value}".update //update возвращает количество измененных строчек
    }
    def insertSql(
        text: PasteText,
        createdAt: TextDate,
        expirationTime: TextDate
    ): Update0 = {
      sql"insert into TEXT (text, created_at, expiration_time) values (${text.content.content}, ${createdAt.date
        .toEpochMilli()}, ${expirationTime.date.toEpochMilli()})".update
    }
  }
  private final class TextDaoImpl extends TextDao {
    override def findById(id: TextId): doobie.ConnectionIO[Option[Text]] =
      sql.findByIdSql(id).option

    override def deleteById(
        id: TextId
    ): doobie.ConnectionIO[Either[TextNotFound, Unit]] =
      sql.deleteByIdSql(id).run.map {
        case 0 => TextNotFound(id).asLeft[Unit]
        case _ => ().asRight[TextNotFound]
      }

    override def createText(
        text: PasteText,
        createdAt: TextDate,
        expirationTime: TextDate
    ): doobie.ConnectionIO[Either[errors.InternalError, Text]] =
      sql
        .insertSql(
          text,
          createdAt,
          expirationTime
        )
        .withUniqueGeneratedKeys[TextId]("id")
        .map(id => Text(id, text.content, createdAt, expirationTime))
        .attemptSomeSqlState { case _ =>
          errors.InternalError(new Throwable("Failed to create text"))
        }
  }

  def make: TextDao = new TextDaoImpl
}
