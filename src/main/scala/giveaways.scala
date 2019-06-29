import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.Done
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.{complete, _}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json.JsValue
import utils.sqliteUtils
import utils.FromMap.to
import utils.database

import scala.io.StdIn

object giveaway {
  // needed to run the route
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.dispatcher

  case class User(id_user: Int, name_user: String, status_blacklist: Int, status_sub: Int)
  case class Users(vec: Vector[User])
  implicit val userFormat = jsonFormat4(User)
  implicit val usersFormat = jsonFormat1(Users)

  case class Giveaway(id_giveaways: Int, id_user: Int, id_user_winner: Int, giveaway: String)
  case class Giveaways(vec: Vector[Giveaway])
  implicit val giveawayFormat = jsonFormat4(Giveaway)
  implicit val giveawaysFormat = jsonFormat1(Giveaways)

  case class GiveawayUser(id_giveaway_users: Int, id_user: Int, id_giveaway: Int)
  case class GiveawayUsers(vec: Vector[GiveawayUser])
  implicit val giveawayUserFormat = jsonFormat3(GiveawayUser)
  implicit val giveawayUsersFormat = jsonFormat1(GiveawayUsers)

  case class ResponseDrawGiveaway(id_giveaway: Int, id_user: Int, status_blacklist: Int, donation: Int)
  case class ResponseDrawGiveaways(vec: Vector[ResponseDrawGiveaway])
  implicit val ResponseDrawGiveawayFormat = jsonFormat4(ResponseDrawGiveaway)
  implicit val ResponseDrawGiveawaysFormat = jsonFormat1(ResponseDrawGiveaways)


  val url = "jdbc:sqlite:./db/project_scala.db"
  val sqliteUtils = new sqliteUtils

  val route_startDb: Route =
    pathPrefix("startDB") {
      get{
        val startDb = new database
        startDb.start_db()
        complete("Database is ready")
      }
    }

  val route_addGiveaway: Route =
    pathPrefix("addGiveaway") {
      post{
        entity(as[JsValue]) { json =>
          val giveaway = json.asJsObject.fields("giveaway").convertTo[String]
          val id_user = json.asJsObject.fields("id_user").convertTo[Int]
          println("giveaway = " + giveaway)

          val query_ct = "SELECT * FROM giveaway;"
          println("query_ct = " + query_ct)

          val res_ct = sqliteUtils.query(url, query_ct, Seq("id_giveaway", "id_user", "id_user_winner", "giveaway"))
          println("res_ct = " + res_ct)

          val res_ctb = res_ct match {
            case Some(r) => r.flatMap(v => to[Giveaway].from(v))
              .count(x => x.giveaway == giveaway.toString && x.id_user == id_user.toInt)
            case _ => None
          }

          println("res_ctb = " +res_ctb)

          if(res_ctb == 0){
            val query = "INSERT INTO giveaway(giveaway, id_user) " +
              "VALUES(\"" + giveaway.toString + "\",\"" + id_user.toInt + "\");"
            println("query = " + query)
            sqliteUtils.query(url, query, Seq())
            complete(json)
          }
          else{
            printf("Error giveaway already exist\n")
            complete("Error giveaway already exist\n")
          }
        }
      }
    }

  val route_subscribeGiveaway: Route =
    pathPrefix("subscribeGiveaway") {
      post{
        entity(as[JsValue]) { json =>
          val id_user = json.asJsObject.fields("id_user").convertTo[Int]
          val id_giveaway = json.asJsObject.fields("id_giveaway").convertTo[Int]

          val query_ct = "SELECT * FROM giveaway_user"
          //println("query_ct = " + query_ct)

          val res_ct = sqliteUtils.query(url, query_ct, Seq("id_giveaway_user", "id_user", "id_giveaway"))

          //println("res_ct = " + res_ct)

          val res_ctb = res_ct match {
            case Some(r) => r.flatMap(v => to[GiveawayUser].from(v))
              .count(x => x.id_user == id_user.toInt && x.id_giveaway == id_giveaway.toInt)
            case _ => None
          }

          println("res_ctb = " + res_ctb)

          if(res_ctb == 0){
            val query = "INSERT INTO giveaway_user(id_user, id_giveaway) " +
              "VALUES(" + id_user.toString + "," + id_giveaway.toString + ");"
            println("query = " + query)
            sqliteUtils.query(url, query, Seq())
            complete(json)
          }
          else{
            printf("Error user has already subscribed\n")
            complete("Error user has already subscribed\n")
          }
        }
      }
    }

  val route_giveawayDraw: Route =
    pathPrefix("giveawayDraw") {
      post{
        entity(as[JsValue]) { json =>
          println("Enter")
          val id_giveaway = json.asJsObject.fields("id_giveaway").convertTo[String]

          val query = "SELECT DISTINCT a.id_user,  a.status_blacklist,  b.id_user,  b.donation,  c.id_giveaway,  c.id_user FROM  user a  JOIN tips b ON a.id_user = b.id_user  JOIN giveaway_user c ON b.id_user = c.id_user"
          println("query = " + query)

          val req = sqliteUtils.query(url, query, Seq("id_user", "status_blacklist", "donation", "id_giveaway"))
          println("req = " + req)

          req match {
            case Some(r) => val values1 = r.flatMap(v => to[ResponseDrawGiveaway].from(v))
              .filter(x => x.status_blacklist == 0 && x.id_giveaway == id_giveaway.toInt)
              .groupBy(x => x.id_user)
              .mapValues(_.map(_.donation.toString.toInt).sum).map(x => (x._1, x._2)).toSeq
              println("values1 = " + values1)
              complete(values1)
            case None => complete("mauvaise table")
          }
        }
      }
    }



  def main(args: Array[String]) {
    val combineRoute =  route_giveawayDraw ~ route_subscribeGiveaway ~ route_addGiveaway ~ route_startDb
    val bindingFuture = Http().bindAndHandle(combineRoute, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and shutdown when done
  }
}
