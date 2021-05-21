name          := """web-blog-project"""
description   := "Back-end project for WEeb blog application"
version       := "1.0-SNAPSHOT"

organization in ThisBuild := "com.krishna"
scalaVersion in ThisBuild := "2.13.6"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= dependencies
  )

lazy val dependencies = Seq(
  guice,
  "com.h2database" % "h2" % "1.4.199",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)