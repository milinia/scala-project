package ru.itis
package domain

import domain.TextToken
import cats.implicits.catsSyntaxOptionId
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import sttp.tapir.Schema

object errors {
  sealed abstract class AppError(
      val message: String,
      val cause: Option[Throwable] = None
  )

  object AppError { //как кодировать/декодировать ошибку
    implicit val encoder: Encoder[AppError] = new Encoder[AppError] {
      override def apply(a: errors.AppError): Json = Json.obj(
        ("error", Json.fromString(a.message))
      )
    }

    implicit val decoder: Decoder[AppError] =
      new Decoder[AppError] { //HCursor позволяет пройтись по json и достать нужные поля
        override def apply(c: HCursor): Result[AppError] =
          c.downField("error").as[String].map(MockError(_))
      }
    implicit val schema: Schema[AppError] = Schema.string[AppError]
  }

  case class TextNotFound(token: TextToken)
      extends AppError(s"Text with ${token.value} id not found")

  case class InternalError(cause0: Throwable)
      extends AppError("Internal error", cause0.some)
  case class MockError(override val message: String) extends AppError(message)
}
