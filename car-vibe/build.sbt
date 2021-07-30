name := """car-vibe"""
organization := "com.pinjamar.cars"

version := "1.0-SNAPSHOT"

val circeVersion = "0.14.1"
val anormVersion = "2.6.10"
val sqliteVersion = "3.36.0.1"
val circePlayVersion = "2814.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

libraryDependencies += guice
libraryDependencies ++= Seq(jdbc, evolutions)
libraryDependencies ++= Seq(
  "org.playframework.anorm" %% "anorm" % anormVersion,
  "org.xerial" % "sqlite-jdbc" % sqliteVersion,
  "com.dripower" %% "play-circe" % circePlayVersion
)

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

