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

object blacklist {

  case class User(id_user: Int, name_user: String, status_blacklist: Int, status_sub: Int)
  case class Users(vec: Vector[User])
  implicit val userFormat = jsonFormat4(User)
  implicit val usersFormat = jsonFormat1(Users)


  val url = "jdbc:sqlite:./db/project_scala.db"
  val sqliteUtils = new sqliteUtils

  val route_blacklistUser: Route =
    pathPrefix("blacklistUser") {
      put {
        entity(as[JsValue]) { json =>
          val id_user = json.asJsObject.fields("id_user").convertTo[String]
          println("id_user = " + id_user)

          val query = "SELECT * FROM user WHERE id_user =" + id_user.toInt + ";"
          println("query = " + query)

          val res = sqliteUtils.query(url, query, Seq())
          println("res = " + res)

          val res_ctb = res match {
            case Some(r) => r.flatMap(v => to[User].from(v))
              .count(x => x.id_user == id_user.toInt)
            case _ => None
          }

          println("res_ctb = " + res_ctb)


          if (res_ctb == 0) {
            val query_ct = "UPDATE user SET status_blacklist = 1 WHERE id_user =" + id_user + ";"
            println("query_ct = " + query_ct)

            val res_ct = sqliteUtils.query(url, query_ct, Seq())
            println("res_ct = " + res_ct)
            complete(json)
          }
          else {
            printf("Error user does not exist\n")
            complete("Error user does not exist\n")
          }
        }
      }
    }
}
