val Http4sVersion = "0.21.24"
val CirceVersion = "0.14.1"
val MunitVersion = "0.7.26"
val MunitCatsEffectVersion = "0.13.0"
val HtmlCleanerVersion = "2.6.1"
val SupertaggedVersion = "2.0-RC2"
val PureconfigVersion = "0.15.0"
val slf4jJdkVersion = "1.7.32"

enablePlugins(JavaAppPackaging)
enablePlugins(GraalVMNativeImagePlugin)

graalVMNativeImageOptions ++= Seq(
  "-H:ResourceConfigurationFiles=" + baseDirectory.value / "graal" / "resource-config.json",
  "--no-fallback"
)

lazy val root = (project in file("."))
  .settings(
    organization := "com.example",
    name := "crawler",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    maintainer := "ashashev@gmail.com",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "org.scalameta" %% "munit" % MunitVersion % Test,
      "org.typelevel" %% "munit-cats-effect-2" % MunitCatsEffectVersion % Test,
      "net.sourceforge.htmlcleaner" % "htmlcleaner" % HtmlCleanerVersion,
      "org.rudogma" %% "supertagged" % SupertaggedVersion,
      "com.github.pureconfig" %% "pureconfig" % PureconfigVersion,
      "org.slf4j" % "slf4j-jdk14" % slf4jJdkVersion,
      "org.scalameta" %% "svm-subs" % "20.2.0"
    ),
    scalacOptions ++= Flags.scalacFlags,
    addCompilerPlugin(
      "org.typelevel" %% "kind-projector" % "0.13.0" cross CrossVersion.full
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )
