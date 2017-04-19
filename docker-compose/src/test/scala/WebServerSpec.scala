import org.scalatest._

import scala.Console._
import scalaj.http.Http
import org.scalatest.Tag
import org.scalatest.exceptions._

import scala.util.Try



class WebServerSpec extends fixture.FunSuite with fixture.ConfigMapFixture with Matchers {

  def getHostInfo(configMap: ConfigMap): String = getContainerSetting(configMap, serviceHostKey)

  def getContainerId(configMap: ConfigMap): String = getContainerSetting(configMap, serviceContainerIdKey)

  def getContainerSetting(configMap: ConfigMap, key: String): String = {
    Try(configMap(key).toString).getOrElse(
      throw new TestFailedException(s"Cannot find the expected Docker Compose service key '$key' in the configMap", 10)
    )
  }

  // The configMap passed to each test case will contain the connection information for the running Docker Compose
  // services. The key into the map is "serviceName:containerPort" and it will return "host:hostPort" which is the
  // Docker Compose generated endpoint that can be connected to at runtime. You can use this to endpoint connect to
  // for testing. Each service will also inject a "serviceName:containerId" key with the value equal to the container id.
  // You can use this to emulate service failures by killing and restarting the container.
  val serviceName = "webapp-scala"
  val serviceHostKey = s"$serviceName:8080"
  val serviceContainerIdKey = s"$serviceName:containerId"

  test("Validate that the Webapp endpoint returns a success code and the string 'Say hello to akka-http from'", DockerComposeTag) {
    configMap => {
      configMap.foreach(println)
      val hostInfo = getHostInfo(configMap)
      val containerId = getContainerId(configMap)

      println(s"Attempting to connect to: $hostInfo, container id is $containerId")

      val output = Http(s"http://$hostInfo").asString
      output.isSuccess shouldBe true
      output.body should include("Say hello to akka-http from")
    }
  }

  test("Validate that /stuff returns things from Mongodb", DockerComposeTag) {
    configMap => {
      val hostInfo = getHostInfo(configMap)

      // insert random name into mongodb
      val http = Http(s"http://$hostInfo/insert").asString
      println(s"Inserted random name ${http.body}")
      http.body should startWith("completed: ")
      val insertedName = http.body.split(' ').last

      // get all data from mongo, expect to see the insertedName
      // you may well see data from previous runs, as mongo is setup to persist
      val output = Http(s"http://$hostInfo/stuff").asString
      output.isSuccess shouldBe true
      println(output.body)
      output.body should include(insertedName)
    }
  }

  test("Validate presence of docker config information in system properties", DockerComposeTag) {
    configMap =>
      Option(System.getProperty(serviceHostKey)) shouldBe defined
  }

  test("This will be ignored under Docker as doesn't have DockerComposeTag") { configMap => }

}