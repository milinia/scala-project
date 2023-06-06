package domainPackage

import derevo.derive
import sttp.tapir.Codec
import sttp.tapir.CodecFormat.TextPlain
import tofu.logging.derivation._

@derive(loggable)
case class RequestContext(
    requestId: String
) //в хедере приходит какой-то request id

object RequestContext {
  implicit val codec: Codec[String, RequestContext, TextPlain] =
    Codec.string.map(RequestContext(_))(_.requestId)
}
