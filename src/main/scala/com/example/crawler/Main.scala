package com.example.crawler

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.catsStdShowForInt
import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._
import io.circe.syntax._
import pureconfig._
import pureconfig.generic.auto._

import com.example.crawler.util.Buildable._

object Main extends IOApp with StrictLogging {
  def run(args: List[String]): IO[ExitCode] =
    for {
     config <- IO.delay(ConfigSource.default.loadOrThrow[Config])
     _ <- IO.delay(logger.info(s"Read config: \n${config.asJson.spaces2}"))
     exitCode <- Server.stream[IO](config).compile.drain.as(ExitCode.Success)
    } yield exitCode
}
