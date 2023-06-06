import cats.data.ReaderT
import cats.effect.kernel.Resource
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.toSemigroupKOps
import com.comcast.ip4s._
import doobie.util.transactor.Transactor
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router
import config.AppConfig
import controller.{TextController, endpoints}
import dao.TextDao
import domainPackage.{RequestContext, domain}
import domain.IOWithRequestContext
import service.TextService
import sttp.tapir.server.http4s.Http4sServerInterpreter
import tofu.logging.Logging

object Main extends IOApp {
  //ресурсы - позволяют получать ресурс и гарантирует последующие ресурсы
  //доступ к бд - ресурс
  //http сервер - ресурс
  private val mainLogs =
    Logging.Make.plain[IO].byName("Main")

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      _ <- Resource.eval(mainLogs.info("Starting Text service..."))
      config <- Resource.eval(AppConfig.load)
      transactor = Transactor //не является ресурсом
        .fromDriverManager[IO](
          config.db.driver,
          config.db.url,
          config.db.user,
          config.db.password
        )
        .mapK[IOWithRequestContext](ReaderT.liftK[IO, RequestContext])
      dao = TextDao.make
      service = TextService.make(dao, transactor)
      controller = TextController.make(service)
      //чтобы он понимал, какой запрос куда должен уходить
      routes = Http4sServerInterpreter[IO]().toRoutes(
        controller.allEndpoints
      ) <+> endpoints.swaggerRoute();
      httpApp = Router("/" -> routes).orNotFound

      _ <- EmberServerBuilder
        .default[IO]
        .withHost(
          Ipv4Address.fromString(config.server.host).getOrElse(ipv4"0.0.0.0")
        )
        .withPort(Port.fromInt(config.server.port).getOrElse(port"80"))
        .withHttpApp(httpApp)
        .build
    } yield ()).useForever.as(ExitCode.Success)
}
