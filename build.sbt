ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.2"

lazy val root = (project in file("."))
  .settings(
    name := "htmx-scala",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "com.lihaoyi" %% "cask"      % "0.10.2",
      "com.lihaoyi" %% "upickle"   % "4.0.2",
    )
  )
