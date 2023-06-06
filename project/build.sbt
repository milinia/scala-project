import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "project",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      cats,
      catsCore,
      doobieCore,
      doobieHikari,
      doobiePostgres,
      tofu,
      tofuDerivation,
      tofuLayout,
      tofuLogback,
      tofuStructured,
      tofuCore,
      tofuCe3,
      tapir,
      core,
      tapirHttpServer,
      tapirJsonCircle,
      newType,
      derevoCircle,
      pureConfig,
      http4sServer,
      swagger
    )
  )

dependencyOverrides += "io.circe" %% "circe-core" % "0.14.3"

scalacOptions ++= Seq("-Ymacro-annotations")

enablePlugins(UniversalPlugin)
enablePlugins(DockerPlugin)
enablePlugins(JavaAppPackaging)

dockerExposedPorts ++= Seq(8080)
dockerBaseImage := "openjdk:17-jdk-slim"
