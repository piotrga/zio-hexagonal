ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "zio-hexagonal",
    libraryDependencies := Seq(
      "dev.zio" %% "zio" % "2.0.9",
      "dev.zio" %% "zio-streams" % "2.0.10",
      "dev.zio" %% "zio-test" % "2.0.10",
      "dev.zio" %% "zio-test-junit" % "2.0.10",
      "dev.zio" %% "zio-macros" % "2.0.10"
    ),
    libraryDependencies += "dev.zio" %% "zio-config" % "4.0.0-RC14",
    libraryDependencies +=
      "dev.zio" %% "zio-config-typesafe" % "4.0.0-RC14",
    libraryDependencies += "dev.zio" %% "zio-config-magnolia" % "4.0.0-RC14",
    scalacOptions += "-Ymacro-annotations"
  )
