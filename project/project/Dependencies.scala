import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.8"
  lazy val cats = "org.typelevel" %% "cats-effect" % "3.5.0"
  lazy val catsCore = "org.typelevel" %% "cats-core" % "2.6.1"
  lazy val doobieCore = "org.tpolecat" %% "doobie-core" % "1.0.0-RC2"
  lazy val doobieHikari =
    "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC2" // HikariCP transactor
  lazy val doobiePostgres =
    "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC2" // Postgres driver 42.3.1 + type mappings
  lazy val tofu = "tf.tofu" %% "tofu-logging" % "0.12.0.1"
  lazy val tofuDerivation = "tf.tofu" %% "tofu-logging-derivation" % "0.12.0.1"
  lazy val tofuLayout = "tf.tofu" %% "tofu-logging-layout" % "0.12.0.1"
  lazy val tofuLogback =
    "tf.tofu" %% "tofu-logging-logstash-logback" % "0.12.0.1"
  lazy val tofuStructured = "tf.tofu" %% "tofu-logging-structured" % "0.12.0.1"
  lazy val tofuCore = "tf.tofu" %% "tofu-core-ce3" % "0.12.0.1"
  lazy val tofuCe3 = "tf.tofu" %% "tofu-doobie-logging-ce3" % "0.12.0.1"
  lazy val newType = "io.estatico" %% "newtype" % "0.4.4"
  lazy val core = "com.softwaremill.sttp.client3" %% "core" % "3.8.15"
  lazy val tapir = "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.4.0"
  lazy val tapirHttpServer =
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.4.0"
  lazy val tapirJsonCircle =
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.4.0"
  lazy val derevo = "tf.tofu" %% "derevo-circe" % "0.13.0"
  lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.17.4"
  lazy val http4sServer = "org.http4s" %% "http4s-ember-server" % "0.23.19"
  lazy val swagger =
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.5.0"
}
