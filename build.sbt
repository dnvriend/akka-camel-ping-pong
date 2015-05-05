name := "akka-camel-ping-pong"

version := "1.0.0-SNAPSHOT"

lazy val pingPongProject = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

libraryDependencies ++=  {
  val akkaV = "2.3.10"
  val activemqV = "5.10.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor"       % akkaV,
    "com.typesafe.akka" %% "akka-camel"       % akkaV,
    "io.spray"          %% "spray-json"       % "1.3.1",
    "org.apache.activemq" % "activemq-camel"  % activemqV,
    "com.typesafe.akka"   %% "akka-testkit"   % akkaV      % Test,
    "org.scalatest"       %% "scalatest"      % "2.2.4"    % Test
  )
}

name in Universal := "ping-pong"
