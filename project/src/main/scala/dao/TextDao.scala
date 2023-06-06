package dao

import domainPackage.domain.{TextDate, TextToken}
import domainPackage.errors.TextNotFound
import domainPackage.{PasteText, Text, errors}

import cats.syntax.either._
import doobie._
import doobie.implicits._

import java.util.UUID

trait TextDao {
  def findByToken(token: TextToken): ConnectionIO[Option[Text]]
  def deleteByToken(token: TextToken): ConnectionIO[Either[TextNotFound, Unit]]
  def createText(
      text: PasteText,
      createdAt: TextDate,
      expirationTime: TextDate
  ): ConnectionIO[Either[errors.InternalError, Text]]
}

object TextDao {
  object sql {
    def findByTokenSql(token: TextToken): Query0[Text] = {
      sql"select * from TEXT where token=${token.value}"
        .query[Text] //query прочитывает ответный тип данных
    }
    def deleteByTokenSql(id: TextToken): Update0 = {
      sql"delete from TEXT where token=${id.value}".update //update возвращает количество измененных строчек
    }
    def insertSql(
        token: String,
        text: PasteText,
        createdAt: TextDate,
        expirationTime: TextDate
    ): Update0 = {
      sql"insert into TEXT (token, text, created_at, expiration_time) values ($token, ${text.content.content}, ${createdAt.date
        .toEpochMilli()}, ${expirationTime.date.toEpochMilli()})".update
    }
  }
  private final class TextDaoImpl extends TextDao {
    override def findByToken(
        token: TextToken
    ): doobie.ConnectionIO[Option[Text]] =
      sql.findByTokenSql(token).option

    override def deleteByToken(
        token: TextToken
    ): doobie.ConnectionIO[Either[TextNotFound, Unit]] =
      sql.deleteByTokenSql(token).run.map {
        case 0 => TextNotFound(token).asLeft[Unit]
        case _ => ().asRight[TextNotFound]
      }

    override def createText(
        text: PasteText,
        createdAt: TextDate,
        expirationTime: TextDate
    ): doobie.ConnectionIO[Either[errors.InternalError, Text]] =
      sql
        .insertSql(
          UUID.randomUUID().toString,
          text,
          createdAt,
          expirationTime
        )
        .withUniqueGeneratedKeys[TextToken]("id")
        .map(id => Text(id, text.content, createdAt, expirationTime))
        .attemptSomeSqlState { case _ =>
          errors.InternalError(new Throwable("Failed to create text"))
        }
  }

  def make: TextDao = new TextDaoImpl
}
