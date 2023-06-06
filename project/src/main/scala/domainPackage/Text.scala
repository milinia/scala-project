package domainPackage

import domain.{TextContent, TextDate, TextToken}

import derevo.circe.{decoder, encoder}
import derevo.derive
import sttp.tapir.Schema
import tofu.logging.derivation._

@derive(loggable, encoder, decoder)
//на уровне рантайма здесь будет просто Long, String, Date
final case class Text(
    token: TextToken,
    content: TextContent,
    createdAt: TextDate,
    expirationTime: TextDate
)
object Text {
  implicit val schema: Schema[Text] = Schema.derived
//  implicit val encoder: Encoder[Text] = deriveEncoder[Text]
//  implicit val decoder: Decoder[Text] = deriveDecoder[Text]
}

@derive(loggable, encoder, decoder)
//тк от пользователя будет приходить только текст (без даты и id)
final case class PasteText(content: TextContent, expirationTime: String)

//object PasteText {
//  implicit val encoder: Encoder[PasteText] = deriveEncoder[PasteText]
//  implicit val decoder: Decoder[PasteText] = deriveDecoder[PasteText]
//}
