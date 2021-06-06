package com.example.crawler

import cats.syntax.either._
import supertagged.TaggedType0
import supertagged.TaggedOps

package object util {

  object NaturalInt extends TaggedType0[Int] {
    def safe(v: Int): Either[String, Type] =
      if (v > 0) TaggedOps(this)(v).asRight
      else s"natural int must b e greater zero, but got $v".asLeft

    def unsafe(v: Int): Type =
      safe(v) match {
        case Left(err) => throw new Exception(err)
        case Right(value) => value
      }

    implicit val buildableForward: Buildable[Type, Raw] =
      Buildable.instance(safe(_), v => v)
  }
  type NaturalInt = NaturalInt.Type

}
