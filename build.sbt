name := "osm-pagerank3"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.vividsolutions" % "jts" % "1.13",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)
