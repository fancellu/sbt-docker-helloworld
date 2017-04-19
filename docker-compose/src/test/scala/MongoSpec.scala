
import org.mongodb.scala.bson.collection.Document
import org.mongodb.scala.{MongoClient, MongoDatabase}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.Console._
import org.scalatest.exceptions._

import scala.util.Try

class MongoSpec extends fixture.FunSuite with fixture.ConfigMapFixture with Matchers with ScalaFutures{

  import scala.concurrent.ExecutionContext.Implicits.global

  def getHostInfo(configMap: ConfigMap): String = getContainerSetting(configMap, serviceHostKey)

  def getContainerId(configMap: ConfigMap): String = getContainerSetting(configMap, serviceContainerIdKey)

  def getContainerSetting(configMap: ConfigMap, key: String): String = {
    Try(configMap(key).toString).getOrElse(
      throw new TestFailedException(s"Cannot find the expected Docker Compose service key '$key' in the configMap", 10)
    )
  }

  val serviceName = "mongo"
  val serviceHostKey = s"$serviceName:27017"
  val serviceContainerIdKey = s"$serviceName:containerId"

  test("Validate that the Mongo endpoint returns the version number in buildInfo'", DockerComposeTag) {
    configMap => {

      val hostInfo = getHostInfo(configMap)
      val containerId = getContainerId(configMap)

      println(s"Attempting to connect to: $hostInfo, container id is $containerId")

      val mongoClient: MongoClient = MongoClient(s"mongodb://$hostInfo")

      val database: MongoDatabase = mongoClient.getDatabase("local")

      val buildInfoF=database.runCommand(Document("buildInfo" -> 1)).toFuture.map(_.head)

      whenReady(buildInfoF){buildInfo=>
        buildInfo.contains("version") shouldBe true
        val ver=buildInfo.getString("version")
        println(s"Version is $ver")
      }
    }
  }

}