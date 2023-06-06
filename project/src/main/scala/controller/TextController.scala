package ru.itis
package controller

import domain.errors.AppError
import service.TextService

import cats.effect.IO
import cats.syntax.either._
import sttp.tapir.server.ServerEndpoint

trait TextController {
  def findTextById: ServerEndpoint[Any, IO]
  def removeTextById: ServerEndpoint[Any, IO] //id придет из эндпоинта
  def createText: ServerEndpoint[Any, IO]

  def allEndpoints: List[ServerEndpoint[Any, IO]]
}

object TextController {
  final private class TextControllerImpl(service: TextService)
      extends TextController {

    override val findTextById: ServerEndpoint[Any, IO] =
      endpoints.findTextById.serverLogic { case (id, ctx) =>
        service.findById(id).map(_.leftMap[AppError](identity)).run(ctx)
      }

    override val removeTextById: ServerEndpoint[Any, IO] =
      endpoints.removeText.serverLogic { case (id, ctx) =>
        service
          .deleteById(id)
          //reader monad в конце использования предоставляешь какое-то значение чтобы запустить
          .run(ctx)
      }

    override val createText: ServerEndpoint[Any, IO] =
      endpoints.createText.serverLogic { case (ctx, text) =>
        service.create(text).run(ctx)
      }

    override val allEndpoints: List[ServerEndpoint[Any, IO]] =
      List(findTextById, removeTextById, createText)
  }

  def make(service: TextService): TextController = new TextControllerImpl(
    service
  )
}
