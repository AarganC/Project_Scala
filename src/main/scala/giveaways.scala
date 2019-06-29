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
import scala.util.Random

import scala.io.StdIn

object giveaway {

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

  val route_addGiveaway: Route =
    pathPrefix("addGiveaway") {
      post{
        entity(as[JsValue]) { json =>
          val giveaway = json.asJsObject.fields("giveaway").convertTo[String]
          val id_user = json.asJsObject.fields("id_user").convertTo[String]
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
          val id_user = json.asJsObject.fields("id_user").convertTo[String]
          val id_giveaway = json.asJsObject.fields("id_giveaway").convertTo[String]

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

          val output = Seq()

          req match {
            case Some(r) => val values = r.flatMap(v => to[ResponseDrawGiveaway].from(v))
              .filter(x => x.status_blacklist == 0 && x.id_giveaway == id_giveaway.toInt)
              .map(_.id_user)
              /*.groupBy(_.id_user).mapValues(t=>(t.map(_.donation).sum))
              .map(x => (x._1, x._2 match {
                case y if 0 until 100 contains y => output :+ Seq(x._1.toString, 0.2)
                case y if 100 until 500 contains y => output :+ Seq(x._1.toString, 0.4)
                case y if 500 until 1000 contains y => output :+ Seq(x._1.toString, 0.6)
                case y if 1000 until 1000000 contains y => output :+ Seq(x._1.toString, 0.8)
                case _ => println("Error attribution weighted; sum_donation = " + x._2.toString)
              }))*/
              val random = new Random
              val reply = values(
                random.nextInt(values.length)
              )
              println("values = " + reply)
              complete(reply.toString)
            case None => complete("mauvaise table")
          }
        }
      }
    }
}
