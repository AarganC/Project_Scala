import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.Done
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import utils.sqliteUtils
import utils.FromMap.to

import scala.io.StdIn

object survey {
  // needed to run the route
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.dispatcher

  case class Donation(id_user: Int, sum_donation: Int)
  case class Donations(vec: Vector[Donation])
  implicit val donationFormat = jsonFormat2(Donation)
  implicit val donationsFormat = jsonFormat1(Donations)

  case class User(id_user: Int, name_user: String, status_blacklist: Int, status_sub: Int)
  case class Users(vec: Vector[User])
  implicit val userFormat = jsonFormat4(User)
  implicit val usersFormat = jsonFormat1(Users)

  def main(args: Array[String]) {
    val url = "jdbc:sqlite:/Users/aargancointepas/Documents/ESGI-4IBD/ProgrammationFonctionnel/Project_Scala/db/project_scala.db"
    val sqliteUtils = new sqliteUtils

    val route: Route =
      pathPrefix("getSumDonationByUser") {
        get {
          val req = sqliteUtils.query(url, "select id_user, name_user, status_blacklist, status_sub from user;", Seq("id_user","name_user", "status_blacklist", "status_sub"))
          req match {
            case Some(r) => val values = r.flatMap(v => to[User].from(v))
              complete(Users(values))
            case None => complete("mauvaise table")
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and shutdown when done
  }
}
