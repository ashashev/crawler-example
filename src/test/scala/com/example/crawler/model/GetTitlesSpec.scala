package com.example.crawler.model

import cats.syntax.either._
import io.circe.Decoder
import io.circe.parser._
import io.circe.syntax._
import munit.FunSuite
import org.http4s.Uri

class GetTitlesSpec extends FunSuite {
  import GetTitlesSpec._

  test("Encode request") {
    val expected = parse(
      raw"""{
        |  "sites": [
        |    "https://ya.ru",
        |    "https://google.com"
        |  ]
        |}""".stripMargin
    ).toOption.get

    val request = GetTitles.Request(
      uri("https://ya.ru") :: uri("https://google.com") :: Nil
    )

    assertEquals(request.asJson, expected)
  }

  test("Decode request") {
    val input = parse(
      raw"""{
        |  "sites": [
        |    "https://ya.ru",
        |    "https://google.com"
        |  ]
        |}""".stripMargin
    ).toOption.get

    val expected: Decoder.Result[GetTitles.Request] = GetTitles
      .Request(
        uri("https://ya.ru") :: uri("https://google.com") :: Nil
      )
      .asRight

    val res = input.as[GetTitles.Request]

    assertEquals(res, expected)
  }

  test("Encode response") {
    val expected = parse(
      raw"""{
        |  "results": [
        |  {
        |    "site": "https://ya.com",
        |    "error": "Not Found"
        |  },
        |  {
        |    "site": "https://google.com",
        |    "title": "Google"
        |  }
        |]
        |}""".stripMargin
    ).toOption.get

    val response = GetTitles.Response(
      (uri("https://ya.com") -> "Not Found".asLeft) ::
        (uri("https://google.com") -> GetTitles.Title("Google").asRight) ::
        Nil
    )

    assertEquals(response.asJson, expected)
  }

  test("Decode response") {
    val input = parse(
      raw"""{
        |  "results": [
        |  {
        |    "site": "https://ya.com",
        |    "error": "Not Found"
        |  },
        |  {
        |    "site": "https://google.com",
        |    "title": "Google"
        |  }
        |]
        |}""".stripMargin
    ).toOption.get

    val expected: Decoder.Result[GetTitles.Response] = GetTitles
      .Response(
        (uri("https://ya.com") -> "Not Found".asLeft) ::
          (uri("https://google.com") -> GetTitles.Title("Google").asRight) ::
          Nil
      )
      .asRight

    val res = input.as[GetTitles.Response]

    assertEquals(res, expected)
  }
}

object GetTitlesSpec {
  def uri(s: String): Uri = Uri.unsafeFromString(s)
}
