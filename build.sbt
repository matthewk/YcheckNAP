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

  // circe 4 play
  "com.dripower" %% "play-circe" % "2609.1",

  // test
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion(scalaBinaryVersion.value) % Test,
  "io.gatling" % "gatling-test-framework" % gatlingVersion(scalaBinaryVersion.value) % Test
)

// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala, GatlingPlugin, JavaAppPackaging, DockerComposePlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    name := """YcheckNAP""",
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation",
    dockerImageCreationTask := (publishLocal in Docker).value,
    mainClass in assembly := Some("play.core.server.ProdServerStart"),
    fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value),
    assemblyMergeStrategy in assembly := {
      case manifest if manifest.contains("MANIFEST.MF") =>
        // We don't need manifest files since sbt-assembly will create
        // one with the given settings
        MergeStrategy.discard
      case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
        // Keep the content for all reference-overrides.conf files
        MergeStrategy.concat
      case x =>
        // For all the other files, use the default sbt-assembly merge strategy
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/index.html
lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
  settings(
    paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
  )
