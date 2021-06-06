package com.example.crawler

import cats.effect.{ConcurrentEffect, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import scala.concurrent.ExecutionContext.global
import com.example.crawler.handler.GetTitles

object Server {

  def stream[F[_]: ConcurrentEffect](config: Config)(implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream

      httpApp = (
        Routes.checkRoutes() <+>
          Routes.getTitles(GetTitles.impl(client, config.getTitles))
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(config.http.port, config.http.address)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain

}
