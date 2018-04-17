classpathTypes += "maven-plugin"
// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.12")

// sbt-paradox, used for documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.3.1")

// Load testing tool:
// http://gatling.io/docs/2.2.2/extensions/sbt_plugin.html
addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.2")

// Scala formatting: "sbt scalafmt"
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.12")

// docker plugin for creating docker images directly
addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.5.0")

// native packager
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.3")

// docker-compose plugin for auto-generation of the docker-compose
// file that will run up the demonstration app
addSbtPlugin("com.tapad" % "sbt-docker-compose" % "1.0.34")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")