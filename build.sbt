val Http4sVersion  = "0.21.34"
val Specs2Version  = "4.15.0"
val LogbackVersion = "1.2.3"
val CirceVersion   = "0.12.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.github.adityaK93",
    name := "transactions",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.http4s"    %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"    %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"    %% "http4s-circe"        % Http4sVersion,
      "io.circe"      %% "circe-generic"       % CirceVersion,
      "io.circe"      %% "circe-literal"       % CirceVersion,
      "org.http4s"    %% "http4s-dsl"          % Http4sVersion,
      "org.specs2"    %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback" % "logback-classic"     % LogbackVersion
    )
  )

ThisBuild / scalaVersion := "2.13.12"
ThisBuild / scalacOptions := Seq("-unchecked", "-deprecation", "-Wunused")
