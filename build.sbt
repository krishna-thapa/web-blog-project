name := """web-blog-project"""
description := "Back-end project for WEeb blog application"
version := "1.0-SNAPSHOT"

organization in ThisBuild := "com.krishna"
scalaVersion in ThisBuild := "2.13.6"

// Run the scalafmt on compile
scalafmtOnCompile := true

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SwaggerPlugin)
  .settings(
    libraryDependencies ++= dependencies ++ mongoDependencies
  )

swaggerDomainNameSpaces := Seq("models")

lazy val dependencies = Seq(
  guice,
  "org.webjars"            % "swagger-ui"          % "3.43.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
)

lazy val mongoDependencies = Seq(
  // Enable reactive mongo for Play 2.8
  "org.reactivemongo" %% "play2-reactivemongo" % "0.20.13-play28",
  // Provide JSON serialization for Joda-Time
  "com.typesafe.play" %% "play-json-joda" % "2.7.4"
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation"
  //"-Xfatal-warnings"
)
