name := "sbt-docker-helloworld"

version := "1.1"

scalaVersion := "2.13.8"

enablePlugins(JavaAppPackaging)

// To read more http://sbt-native-packager.readthedocs.io/en/latest/formats/docker.html

enablePlugins(DockerPlugin)

// this is much smaller than the official java8 image

dockerBaseImage := "openjdk:8-jre-alpine"

import com.typesafe.sbt.packager.docker.Cmd

// we need bash but alpine java8 doesn't have it by default
// so we inject after the FROM, (append not good enough, too late at the end)

dockerCommands := dockerCommands.value.flatMap{
  case cmd@Cmd("FROM",_) => List(cmd,Cmd("RUN", "apk update && apk add bash"))
  case other => List(other)
}