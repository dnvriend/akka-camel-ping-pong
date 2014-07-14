organization := "com.github.dnvriend"

name := "akka-camel-ping-pong"

version := "0.0.1"

isSnapshot := true

scalaVersion := "2.11.1"

libraryDependencies ++= {
  val scalaV = "2.11.1"
  val akkaV = "2.3.4"
  val activemqV = "5.10.0"
  Seq(
    "org.scala-lang" % "scala-library" % scalaV,
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-camel" % akkaV,
    "com.typesafe" % "config" % "1.2.0",
    "org.apache.activemq" % "activemq-all" % activemqV,
    "org.apache.activemq" % "activemq-camel" % activemqV
  )
}

autoCompilerPlugins := true

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

publishMavenStyle := true

publishArtifact in Test := false

net.virtualvoid.sbt.graph.Plugin.graphSettings

com.github.retronym.SbtOneJar.oneJarSettings

net.virtualvoid.sbt.graph.Plugin.graphSettings