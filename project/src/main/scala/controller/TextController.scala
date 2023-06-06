package controller

import domainPackage.errors.AppError
import service.TextService

import cats.effect.IO
import cats.syntax.either._
import sttp.tapir.server.ServerEndpoint

trait TextController {
  def findTextByToken: ServerEndpoint[Any, IO]
  def removeTextByToken: ServerEndpoint[Any, IO] //id придет из эндпоинта
  def createText: ServerEndpoint[Any, IO]

  def allEndpoints: List[ServerEndpoint[Any, IO]]
}

object TextController {
  final private class TextControllerImpl(service: TextService)
      extends TextController {

    override val findTextByToken: ServerEndpoint[Any, IO] =
      endpoints.findTextByToken.serverLogic { case (token, ctx) =>
        service.findByToken(token).map(_.leftMap[AppError](identity)).run(ctx)
      }

    override val removeTextByToken: ServerEndpoint[Any, IO] =
      endpoints.removeTextByToken.serverLogic { case (token, ctx) =>
        service
          .deleteByToken(token)
          //reader monad в конце использования предоставляешь какое-то значение чтобы запустить
          .run(ctx)
      }

    override val createText: ServerEndpoint[Any, IO] =
      endpoints.createText.serverLogic { case (ctx, text) =>
        service.create(text).run(ctx)
      }

    override val allEndpoints: List[ServerEndpoint[Any, IO]] =
      List(findTextByToken, removeTextByToken, createText)
  }

  def make(service: TextService): TextController = new TextControllerImpl(
    service
  )
}
