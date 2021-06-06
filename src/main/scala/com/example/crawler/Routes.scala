package com.example.crawler

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

import com.example.crawler.handler
import com.example.crawler.model

object Routes {
  def checkRoutes[F[_]: Sync](): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> Root / "check" => Ok("OK") }
  }

  def getTitles[F[_]: Sync](h: handler.GetTitles.Handler[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case req @ POST -> Root / "get-titles" =>
      Ok(
        for {
          r <- req.as[model.GetTitles.Request]
          resp <- h(r)
        } yield resp
      )
    }
  }
}
