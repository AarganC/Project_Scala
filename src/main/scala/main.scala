import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.Done
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json.JsValue
import utils.sqliteUtils
import utils.FromMap.to

import scala.io.StdIn
import survey._
import blacklist._
import giveaway._
import tips._

import scala.io.StdIn

object Main {

  // needed to run the route
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.dispatcher


  def main(args: Array[String]): Unit = {
    val combineRoute = route_addSurvey ~ route_addAnswer ~ route_getResume ~ route_getSpeResume ~ route_startDb ~ route ~ route_addGiveaway ~ route_blacklistUser
    val bindingFuture = Http().bindAndHandle(combineRoute, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and shutdown when done
  }
}
