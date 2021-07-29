name := """play-scala-seed"""
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
  "org.xerial" % "sqlite-jdbc" % sqliteVersion
)

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.pinjamar.cars.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.pinjamar.cars.binders._"
