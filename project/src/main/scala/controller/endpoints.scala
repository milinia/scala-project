package controller

import domainPackage.{PasteText, RequestContext, Text}
import domainPackage.domain.TextToken
import domainPackage.errors.AppError

import cats.effect.IO
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

//описание эндпоинтов
object endpoints {
  //input error_output output параметр_окружения
  val findTextByToken
      : PublicEndpoint[(TextToken, RequestContext), AppError, Option[
        Text
      ], Any] =
    endpoint.get
      .in("text" / path[TextToken])
      //хотим уметь видеть одного пользователя(индификатор одного запроса)
      .in(header[RequestContext]("X-Request-Id"))
      .errorOut(jsonBody[AppError])
      .out(jsonBody[Option[Text]])

  val removeTextByToken
      : PublicEndpoint[(TextToken, RequestContext), AppError, Unit, Any] =
    endpoint.delete
      .in("text" / path[TextToken])
      .in(header[RequestContext]("X-Request-Id"))
      .errorOut(jsonBody[AppError])

  val createText
      : PublicEndpoint[(RequestContext, PasteText), AppError, Text, Any] =
    endpoint.post
      .in("text")
      .in(header[RequestContext]("X-Request-Id"))
      .in(jsonBody[PasteText])
      .errorOut(jsonBody[AppError])
      .out(jsonBody[Text])

  def swaggerRoute(): HttpRoutes[IO] = {
    val endpoints =
      List(
        findTextByToken,
        removeTextByToken,
        createText
      )

    val swaggerEndpoints =
      SwaggerInterpreter().fromEndpoints[IO](endpoints, "Library", "1.0")

    Http4sServerInterpreter[IO]().toRoutes(swaggerEndpoints)
  }
}
