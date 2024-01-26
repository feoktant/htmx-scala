ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "htmx-scala",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.12.0",
      "com.lihaoyi" %% "cask"      % "0.9.2",
      "com.lihaoyi" %% "upickle"   % "3.1.4",
    )
  )
