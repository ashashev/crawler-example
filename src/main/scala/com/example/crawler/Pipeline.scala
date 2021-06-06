package com.example.crawler

import cats.effect.Concurrent
import cats.syntax.all._
import org.htmlcleaner.HtmlCleaner
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.UnexpectedStatus

object Pipeline {

  def download[F[_]: Concurrent](site: Uri, client: Client[F]): F[(Uri, Either[String, String])] =
    client
      .expect[String](site)
      .map(_.asRight[String])
      .recover {
        case e: UnexpectedStatus => e.status.reason.asLeft
        case e => Option(e.getMessage()).getOrElse("Unknown error").asLeft
      }
      .map(site -> _)

  private val cleaner = new HtmlCleaner

  def extractTitle(html: String): Option[String] =
    Option(cleaner.clean(html).findElementByName("title", true))
      .flatMap(tn => Option(tn.getText().toString()))
}
