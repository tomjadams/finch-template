name := "finch-template"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.11.0-SNAPSHOT" changing(),
  "com.github.finagle" %% "finch-circe" % "0.11.0-SNAPSHOT" changing(),
  "com.twitter" %% "twitter-server" % "1.20.0",
  "com.netaporter" %% "scala-uri" % "0.4.14",

  //  "io.circe" %% "circe-core" % "0.4.1",
  "io.circe" %% "circe-generic" % "0.4.1",
  //  "io.circe" %% "circe-parser" % "0.4.1",

  // Utilities
  "joda-time" % "joda-time" % "2.9.3",
  "org.joda" % "joda-convert" % "1.8",

  // Logging
  "ch.qos.logback" % "logback-core" % "1.1.7",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.slf4j" % "slf4j-api" % "1.7.21",

  // Testing
  "org.specs2" %% "specs2-core" % "3.8.3" % "test",
  "org.specs2" %% "specs2-mock" % "3.8.3" % "test",
  "org.specs2" %% "specs2-scalacheck" % "3.8.3" % "test",
  "org.specs2" %% "specs2-cats" % "3.8.3" % "test",
  "org.hamcrest" % "hamcrest-core" % "1.3" % "test",
  "org.mockito" % "mockito-all" % "1.10.19" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
)

resolvers += "Twitter" at "http://maven.twttr.com"

resolvers += Resolver.sonatypeRepo("snapshots")

scalacOptions ++= Seq("-Xlint", "-unchecked", "-deprecation")

//fork in test := true

scalacOptions in Test ++= Seq("-Yrangepos")

//parallelExecution in Test := false

testOptions in Test += Tests.Setup(() => System.setProperty("ENV", "test"))

//javaOptions in Test := Seq("-DENV=test")

seq(Revolver.settings: _*)

enablePlugins(JavaAppPackaging)
