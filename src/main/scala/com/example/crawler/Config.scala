package com.example.crawler

import com.example.crawler.util.NaturalInt

final case class GetTitlesConfig(loadingConcurrent: NaturalInt, titleConcurrent: NaturalInt)

final case class HttpConfig(address: String, port: Int)

final case class Config(http: HttpConfig, getTitles: GetTitlesConfig)
