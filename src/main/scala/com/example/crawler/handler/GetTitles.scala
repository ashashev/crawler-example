package com.example.crawler.handler

import cats.effect.Concurrent
import cats.syntax.all._
import fs2.Stream
import org.htmlcleaner.HtmlCleaner
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.UnexpectedStatus

import com.example.crawler.GetTitlesConfig
import com.example.crawler.model.GetTitles._

object GetTitles {

  def download[F[_]: Concurrent](client: Client[F]): Uri => F[(Uri, Either[String, String])] =
    site =>
      client
        .expect[String](site)
        .map(_.asRight[String])
        .recover {
          case e: UnexpectedStatus => e.status.reason.asLeft
          case e => Option(e.getMessage()).getOrElse("Unknown error").asLeft
        }
        .map(site -> _)

  private val cleaner = new HtmlCleaner

  def extractTitle(html: String): Option[Title] =
    Option(cleaner.clean(html).findElementByName("title", true))
      .flatMap(tn => Option(tn.getText().toString()).map(Title(_)))

  trait Handler[F[_]] {
    def apply(req: Request): F[Response]
  }

  def impl[F[_]: Concurrent](client: Client[F], config: GetTitlesConfig): Handler[F] =
    new Handler[F] {
      final def apply(req: Request): F[Response] =
        Stream[F, Uri](req.sites: _*)
          .parEvalMapUnordered(config.loadingConcurrent)(download(client))
          .parEvalMapUnordered(config.titleConcurrent) { case (uri, htmlRes) =>
            Concurrent[F].pure(
              uri -> htmlRes.flatMap(extractTitle(_).toRight("the title tag was not found"))
            )
          }
          .compile
          .toList
          .map(Response(_))
    }
}
