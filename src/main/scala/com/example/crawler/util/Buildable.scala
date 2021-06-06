package com.example.crawler.util

import scala.reflect.ClassTag
import scala.reflect.classTag

import io.circe.{Decoder, Encoder}
import pureconfig._
import cats.Show
import cats.syntax.show._

trait Buildable[Type, From] {
  def forward(v: From): Either[String, Type]
  def backward(v: Type): From
}

object Buildable {
  def instance[Type, From](
    fwd: From => Either[String, Type],
    bkw: Type => From
  ): Buildable[Type, From] =
    new Buildable[Type, From] {
      override def forward(v: From): Either[String, Type] = fwd(v)
      override def backward(v: Type): From = bkw(v)
    }

  implicit def decoderBuildable[Type, From](
    implicit b: Buildable[Type, From],
    fDec: Decoder[From]
  ): Decoder[Type] = fDec.emap(b.forward(_))

  implicit def encoderBuildable[Type, From](
    implicit b: Buildable[Type, From],
    fEnc: Encoder[From]
  ): Encoder[Type] = fEnc.contramap(b.backward(_))

  implicit def configReader[Type: ClassTag, From: Show](
    implicit b: Buildable[Type, From],
    fcf: ConfigReader[From]
  ): ConfigReader[Type] =
    fcf.emap(v =>
      b.forward(v)
        .left
        .map(err =>
          error.CannotConvert(
            value = v.show,
            toType = classTag[Type].runtimeClass.getCanonicalName(),
            because = err
          )
        )
    )
}
