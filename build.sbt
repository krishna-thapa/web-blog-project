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
    libraryDependencies ++= dependencies ++ mongoDependencies,
    // Run tests in a separate JVM from sbt.
    // This allows for graceful shutdown of containers once the tests have finished running
    Test / fork := true
  )

swaggerDomainNameSpaces := Seq("models", "forms")

lazy val dependencies = Seq(
  guice,
  "org.webjars"            % "swagger-ui"                      % "3.43.0",
  "org.scalatestplus.play" %% "scalatestplus-play"             % "5.1.0" % Test,
  "com.dimafeng"           %% "testcontainers-scala-scalatest" % "0.38.8" % Test,
  "com.dimafeng"           %% "testcontainers-scala-mongodb"   % "0.38.8" % Test
)

lazy val mongoDependencies = Seq(
  // Enable reactive mongo for Play 2.8
  "org.reactivemongo" %% "play2-reactivemongo" % "1.0.4-play28",
  // Provide JSON serialization for reactive mongo
  "org.reactivemongo" %% "reactivemongo-play-json-compat" % "1.0.4-play28"
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
