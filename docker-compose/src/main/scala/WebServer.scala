
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.mongodb.scala.bson.collection.Document

import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.util.Random

object WebServer extends App{

  implicit val system = ActorSystem("akka-http-actor-system")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  val mongoClient: MongoClient = MongoClient(s"mongodb://mongo:27017")

  val database: MongoDatabase = mongoClient.getDatabase("local")
  val collection: MongoCollection[Document] = database.getCollection("stuff")

  val route =
    pathSingleSlash {
      get {
        val localhostname = java.net.InetAddress.getLocalHost.getHostName
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Say hello to akka-http from $localhostname</h1>"))
      }
    } ~
    path("stuff"){
      get {
        val stringFut=for  {
          string<-collection.find().toFuture().map(docs=>docs.map(_.toJson).mkString("\n"))
        } yield s"JSON from stuff collection\n$string"
        complete(stringFut)
      }
    } ~ path("insert"){
      get {
        val name= Random.alphanumeric.take(10).mkString
        val doc: Document = Document("name" -> name)
        val futComplete=collection.insertOne(doc).toFuture()
        complete(futComplete.map(seq=>s"completed: $name"))
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

  println(s"Server online on 8080")

}
