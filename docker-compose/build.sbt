name := "webapp-scala"

version := "1.0.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.5"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1"

enablePlugins(JavaAppPackaging, DockerComposePlugin)

dockerImageCreationTask := (publishLocal in Docker).value

dockerBaseImage := "frolvlad/alpine-oraclejdk8"

import com.typesafe.sbt.packager.docker.Cmd

// we need bash but alpine java8 doesn't have it by default
// so we inject after the FROM, (append not good enough, too late at the end)

dockerCommands := dockerCommands.value.flatMap{
  case cmd@Cmd("FROM",_) => List(cmd,Cmd("RUN", "apk update && apk add bash"))
  case other => List(other)
}