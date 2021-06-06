package com.example.crawler.model

import cats.effect.Sync
import cats.syntax.either._
import com.example.crawler.util.Buildable
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.Uri
import org.http4s.circe._
import supertagged.TaggedType

object GetTitles {

  object Title extends TaggedType[String] {
    implicit def buildable: Buildable[Type, String] =
      Buildable.instance(v => Title(v).asRight, v => v)
  }
  type Title = Title.Type

  final case class Request(sites: List[Uri])

  object Request {
    implicit val requestDecoder: Decoder[Request] = deriveDecoder[Request]
    implicit val requestEncoder: Encoder[Request] = deriveEncoder[Request]

    implicit def requestEntityDecoder[F[_]: Sync]: EntityDecoder[F, Request] = jsonOf
    implicit def requestEntityEncoder[F[_]]: EntityEncoder[F, Request] = jsonEncoderOf
  }

  final case class Response(results: List[(Uri, Either[String, Title])])

  implicit val responseInnerEncoder: Encoder[(Uri, Either[String, Title])] =
    new Encoder[(Uri, Either[String, Title])] {
      final def apply(v: (Uri, Either[String, Title])): Json =
        Json.obj(
          "site" -> Json.fromString(v._1.toString()),
          v._2 match {
            case Left(value) => "error" -> Json.fromString(value)
            case Right(value) => "title" -> Json.fromString(value)
          }
        )
    }

  implicit val responseInnerDecoder: Decoder[(Uri, Either[String, Title])] =
    new Decoder[(Uri, Either[String, Title])] {
      final def apply(c: HCursor): Decoder.Result[(Uri, Either[String, Title])] =
        for {
          site <- c.downField("site").as[Uri]
          res <- c.downField("title").as[String].map(Title(_).asRight[String]).recoverWith {
            case _ =>
              c.downField("error").as[String].map(_.asLeft)
          }
        } yield site -> res
    }

  object Response {
    implicit val responseDecoder: Decoder[Response] = deriveDecoder[Response]
    implicit val responseEncoder: Encoder[Response] = deriveEncoder[Response]

    implicit def responseEntityDecoder[F[_]: Sync]: EntityDecoder[F, Response] = jsonOf
    implicit def responseEntityEncoder[F[_]]: EntityEncoder[F, Response] = jsonEncoderOf
  }
}
