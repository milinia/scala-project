package ru.itis
package domain

import cats.data.ReaderT
import cats.effect.IO
import derevo.circe.{decoder, encoder}
import derevo.derive
import doobie.util.Read
import io.estatico.newtype.macros.newtype
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.{Codec, Schema}
import tofu.logging.derivation._

import java.time.Instant

object domain {
  //@derive(loggable, encoder, decoder) - https://docs.tofu.tf/docs/tofu.logging.home/
  //loggable - чтобы понимать, как логировать класс
  //encoder и decoder - для json
  //@newtype - на уровне компилятора(просто кейс класс с одним полем) Long
  // будет оборачиваться как тип TextId, в рантайме как Long(можно запутаться
  // в артибутах класса, а через оборачивание точно не перепутать)
  @derive(loggable, encoder, decoder)
  @newtype
  case class TextId(value: Long)
  object TextId {
    implicit val doobieRead: Read[TextId] =
      Read[Long].map(
        TextId(_)
      ) //нужно чтобы doobie понимал как преобразовывать значения
    implicit val schema: Schema[TextId] =
      Schema.schemaForLong.map(l => Some(TextId(l)))(
        _.value
      ) //маппинг из лонга в TextId
    implicit val codec: Codec[String, TextId, TextPlain] =
      Codec.long.map(TextId(_))(_.value)
  }
  @derive(loggable, encoder, decoder)
  @newtype
  case class TextContent(content: String)
  object TextContent {
    implicit val doobieRead: Read[TextContent] =
      Read[String].map(TextContent(_))
    implicit val schema: Schema[TextContent] =
      Schema.schemaForString.map(n => Some(TextContent(n)))(_.content)
  }
  @derive(loggable, encoder, decoder)
  @newtype
  case class TextDate(date: Instant)
  object TextDate {
    implicit val doobieRead: Read[TextDate] =
      Read[Long].map(ts => TextDate(Instant.ofEpochMilli(ts)))

    implicit val schema: Schema[TextDate] = Schema.schemaForString.map(n =>
      Some(TextDate(Instant.parse(n)))
    )(_.date.toString)
  }

  type IOWithRequestContext[A] =
    ReaderT[IO, RequestContext, A] //принимает эффект, вход и выход
}
