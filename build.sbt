//import Dependencies._

val Versions = new {
  val junitJupiter = "5.1.0"
  val junitPlatform = "1.1.0"
  val junitVintage = "5.1.0"
  val testInterface = "1.0"
}

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.1",
      version      := "0.1.0-SNAPSHOT",
      javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
      // For project with only Java sources. In order to compile Scala sources, remove the following two lines.
      crossPaths := false,
      autoScalaLibrary := false
    )),
    name := "s4n-java-training",
    libraryDependencies ++= Seq(
      "io.vavr" % "vavr" % "0.9.2",
      "org.junit.platform" % "junit-platform-runner" % "1.0.0-M3" % "test",
      "org.junit.jupiter" % "junit-jupiter-engine" % "5.0.0-M3" % "test",
      "org.junit.vintage" % "junit-vintage-engine" % "4.12.0-M3" % "test",
      "com.novocode" % "junit-interface" % "0.11" % "test",
      //"junit" % "junit" % "4.12" % "test",
      //"org.junit" % "junit5-engine" % "5.0.0-ALPHA",
      //"org.junit.jupiter" % "junit-jupiter-api" % "5.2.0" % "test",
      //"com.novocode" % "junit-interface" % "0.11" % "test",
      //"org.junit.platform" % "junit-platform-runner" % Versions.junitPlatform,
      //"org.junit.jupiter" % "junit-jupiter-engine" % Versions.junitJupiter,
      //"org.scala-sbt" % "test-interface" % Versions.testInterface,
      //"org.junit.jupiter" % "junit-jupiter-params" % Versions.junitJupiter % Test,
      //"org.hamcrest" % "hamcrest-library" % "1.3" % Test,
      //"org.mockito" % "mockito-core" % "2.7.22" % Test,
      //"com.novocode" % "junit-interface" % "0.11" % Test,
      //"org.junit.vintage" % "junit-vintage-engine" % "4.12.0-M3" % "test",
      "org.projectlombok" % "lombok" % "1.16.16"
    )
  )
