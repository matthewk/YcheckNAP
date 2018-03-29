import sbt.Keys._

lazy val GatlingTest = config("gatling") extend Test

scalaVersion in ThisBuild := "2.12.4"
crossScalaVersions := Seq("2.11.12", "2.12.4")

def gatlingVersion(scalaBinVer: String): String = scalaBinVer match {
  case "2.11" => "2.2.5"
  case "2.12" => "2.3.0"
}

libraryDependencies ++= Seq(
  guice,
  "org.joda" % "joda-convert" % "1.9.2",
  "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
  "io.lemonlabs" %% "scala-uri" % "1.1.1",
  "net.codingwell" %% "scala-guice" % "4.1.1",

  // play-monadic-actions ++ cats
  "io.kanaka" %% "play-monadic-actions" % "2.1.0",
  "io.kanaka" %% "play-monadic-actions-cats" % "2.1.0",
  "org.typelevel" %% "cats-core" % "1.0.0-MF",

  // test
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion(scalaBinaryVersion.value) % Test,
  "io.gatling" % "gatling-test-framework" % gatlingVersion(scalaBinaryVersion.value) % Test
)

// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala, GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    name := """play-scala-rest-api-example""",
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation"
  )

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/index.html
lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
  settings(
    paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
  )
