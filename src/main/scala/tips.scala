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

object tips {

  case class Donation(id_user: Int, donation: Int)
  case class Donations(vec: Vector[Donation])
  case class DonationsUsers(users: Map[String,String])
  implicit val donationFormat = jsonFormat2(Donation)
  implicit val donationsFormat = jsonFormat1(Donations)
  implicit val donationsUsersFormat = jsonFormat1(DonationsUsers)


  case class Tip(id_user: Int,id_donation: Int, donation: Int)
  case class Tips(vec: Vector[Tip])
  implicit val tipFormat = jsonFormat3(Tip)
  implicit val tipsFormat = jsonFormat1(Tips)

  case class User(id_user: Int, name_user: String,status_blacklist: Int,status_sub: Int)
  case class Users(vec: Vector[User])
  implicit val userFormat = jsonFormat4(User)
  implicit val usersFormat = jsonFormat1(Users)


  case class Add_Don(id_user: Int, somme: Int)
  case class Add_Dons(vec: Vector[Add_Don])
  implicit val Add_DonFormat = jsonFormat2(Add_Don)
  implicit val Add_DonsFormat = jsonFormat1(Add_Dons)

  val url = "jdbc:sqlite:./db/project_scala.db"
  val sqliteUtils = new sqliteUtils

  val route: Route = concat(

    /*○ Récupérer la liste de tous les donateurs (liste de users)
    ○ Réaliser un don
     ○ Annuler un don
     ○ Faire la somme de tous les dons
     ○ Faire la somme de tous les dons par utilisateur
     ○ Faire la somme de tous les dons d’un utilisateur
     */


    // Récupérer la liste de tous les donateurs (liste de users)XXXX
    pathPrefix("getAllUser") {
      get {
        val req = sqliteUtils.query(url, "SELECT * FROM user;", Seq("id_user","name_user","status_blacklist","status_sub"))
        req match {
          case Some(r) => val values = r.flatMap(v => to[User].from(v))
            complete(Users(values))
          case None => complete("mauvaise table")
        }
      }
    }
    ,
    // ○ Réaliser un don
    path("AddTips") {
      post {
        entity(as[JsValue]) { json =>
          val id_user = json.asJsObject.fields("id_user").convertTo[String]
          val somme = json.asJsObject.fields("somme").convertTo[String]
          val req = "INSERT INTO tips VALUES(NULL," + id_user.toString + "," + somme.toString + ");"
          val response = sqliteUtils.query(url, req, Seq("id_tips", "id_user", "montant"))
          complete("Le don de l'utilisateur numéro :" + id_user.toString + ", d'un montant de " + somme.toString + " euros a bien été effectué !")
        }
      }
    },

    // ○ Annuler un don
    path("DeleteTips") {
      post {
        entity(as[JsValue]) { json =>
          val id_tips = json.asJsObject.fields("id_tips").convertTo[String]
          val req = "Delete from tips where id_donation= "+id_tips+";"
          val response = sqliteUtils.query(url, req, Seq("id_tips", "id_user", "montant"))
          complete("Le don numéro :"+id_tips+" à bien été supprimé !")
        }
      }
    },



    //Récupérer la liste de tous les abonnées (liste de users)XXXX
    pathPrefix("getAllSubs") {
      get {
        val req = sqliteUtils.query(url, "SELECT * FROM user;", Seq("id_user","name_user","status_blacklist","status_sub"))
        req match {
          case Some(r) => val values = r.flatMap(v => to[User].from(v))
            val users=Users(values).vec.filter(_.status_sub ==1)
            complete(users)
          case None => complete("mauvaise table")
        }
      }
    }
    ,
    //○ Faire la somme de tous les dons XXXX

    pathPrefix("getSumDonation"){
      get {
        val req = sqliteUtils.query(url, "SELECT * FROM tips;", Seq("id_user","donation"))
        req match {
          case Some(r) => val values = r.flatMap(v => to[Donation].from(v))
            complete(values.map(_.donation).sum.toString())
          case None => complete("mauvaise table")
        }
      }
    },
    // Faire la somme de tous les dons par utilisateurXXX
    pathPrefix("getSumDonationUser"){
      get {
        val req = sqliteUtils.query(url, "SELECT * FROM tips;", Seq("id_user","donation"))
        req match {
          case Some(r) => val values = r.flatMap(v => to[Donation].from(v))
            val donationsVec = Donations(values).vec
              .groupBy(_.id_user).mapValues(t=>(t.map(_.donation).sum))
              .map(t => (t._1.toString(), t._2.toString()))
            complete(DonationsUsers(donationsVec))
          case None => complete("mauvaise table")
        }
      }
    }

    //Faire la somme de tous les dons pour un utilisateur


  )
}
