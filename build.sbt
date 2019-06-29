name := "Project_Scala"

version := "0.1"

scalaVersion := "2.12.8"

mainClass in Compile := Some("Main")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.3",
  "org.xerial" % "sqlite-jdbc" % "3.25.2" ,
  "com.typesafe.akka" %% "akka-http"   % "10.1.8",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.19",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.8"
)

