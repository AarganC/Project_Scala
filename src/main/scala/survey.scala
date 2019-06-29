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

object survey {

  case class User(id_user: Int, name_user: String, status_blacklist: Int, status_sub: Int)
  case class Users(vec: Vector[User])
  implicit val userFormat = jsonFormat4(User)
  implicit val usersFormat = jsonFormat1(Users)

  case class Survey(id_survey: Int, survey: String, answer1: String, answer2: String)
  case class Surveys(vec: Vector[Survey])
  implicit val surveyFormat = jsonFormat4(Survey)
  implicit val surveysFormat = jsonFormat1(Surveys)

  case class SurveyAnswer(id_survey_answer: Int, id_user: Int, response: Int, id_survey: Int)
  case class SurveyAnswers(vec: Vector[SurveyAnswer])
  implicit val surveyAnswerFormat = jsonFormat4(SurveyAnswer)
  implicit val surveyAnswersFormat = jsonFormat1(SurveyAnswers)

  case class ResponseAnswer(survey: String, answer1: String, answer2: String, response: Int)
  case class ResponseAnswers(vec: Vector[ResponseAnswer])
  implicit val responseAnswerFormat = jsonFormat4(ResponseAnswer)
  implicit val responseAnswersFormat = jsonFormat1(ResponseAnswers)

  case class ResponseSpeAnswer(survey: String, answer1: String, answer2: String, response: Int, id_survey: Int)
  case class ResponseSpeAnswers(vec: Vector[ResponseSpeAnswer])
  implicit val responseSpeAnswerFormat = jsonFormat5(ResponseSpeAnswer)
  implicit val responseSpeAnswersFormat = jsonFormat1(ResponseSpeAnswers)

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

  val route_addSurvey: Route =
    pathPrefix("addSurvey") {
      post{
        entity(as[JsValue]) { json =>
          val survey = json.asJsObject.fields("survey").convertTo[String]
          val answer1 = json.asJsObject.fields("answer1").convertTo[String]
          val answer2 = json.asJsObject.fields("answer2").convertTo[String]
          println("survey = " + survey)

          val query_ct = "SELECT * FROM survey;"
          println("query_ct = " + query_ct)

          val res_ct = sqliteUtils.query(url, query_ct, Seq("id_survey", "survey", "answer1", "answer2"))
          println("res_ct = " + res_ct)

          val res_ctb = res_ct match {
            case Some(r) => r.flatMap(v => to[Survey].from(v))
              .count(x => x.survey == survey && x.answer1 == answer1 && x.answer2 == answer2)
            case _ => None
          }

          if(res_ctb == 0){
            val query = "INSERT INTO survey(survey, answer1, answer2) " +
              "VALUES(\"" + survey.toString() + "\",\"" + answer1.toString + "\",\"" + answer2.toString + "\");"
            println("query = " + query)
            sqliteUtils.query(url, query, Seq())
            complete(json)
          }
          else{
            printf("Error survey already exist\n")
            complete("Error survey already exist\n")
          }
        }
      }
    }

  val route_addAnswer: Route =
    pathPrefix("addAnswer") {
      post{
        entity(as[JsValue]) { json =>
          val id_user = json.asJsObject.fields("id_user").convertTo[String]
          val response = json.asJsObject.fields("response").convertTo[String]
          val id_survey = json.asJsObject.fields("id_survey").convertTo[String]

          val query_ct = "SELECT * FROM survey_answer "
          //println("query_ct = " + query_ct)

          val res_ct = sqliteUtils.query(url, query_ct, Seq("id_survey_answer", "id_user", "response", "id_survey"))

          //println("res_ct = " + res_ct)

          val res_ctb = res_ct match {
            case Some(r) => r.flatMap(v => to[SurveyAnswer].from(v))
              .count(x => x.id_user == id_user.toInt && x.response == response.toInt && x.id_survey == id_survey.toInt)
            case _ => None
          }

          //println("res_ctb = " + res_ctb)

          if(res_ctb.toString.toInt == 0){
            val query = "INSERT INTO survey_answer(id_user, response, id_survey) " +
              "VALUES(" + id_user.toString + "," + response.toString + "," + id_survey.toString + ");"
            println("query = " + query)
            sqliteUtils.query(url, query, Seq())
            complete(json)
          }
          else{
            printf("Error user has already participated\n")
            complete("Error user has already participated\n")
          }
        }
      }
    }

  val route_getResume: Route =
    pathPrefix("getResume") {
      get{
        val req = sqliteUtils.query(url, "SELECT survey, answer1, answer2, response  FROM survey_answer a " +
          "JOIN survey s WHERE a.id_survey = s.id_survey;", Seq("survey", "answer1", "answer2", "response"))
        println("req = " + req)
        req match {
          case Some(r) => val values1 = r.flatMap(v => to[ResponseAnswer].from(v)).groupBy(x => (x.response, x.answer1, x.answer2, x.survey))
            .map(t => (t._1._4.toString(), t._1._1 match { case 0 => t._1._2; case 1 => t._1._3 }, t._2.length.toString.toInt))
            println("values1 = " + values1)
            complete(values1)
          //case Some(r) => val values1 = r.flatMap(v => to[SurveyAnswer].from(v))
          //  complete(values1)
          case None => complete("mauvaise table")
        }
      }
    }

  val route_getSpeResume: Route =
    pathPrefix("getSpeResume") {
      post{
        entity(as[JsValue]) { json =>
          val id_survey = json.asJsObject.fields("id_survey").convertTo[String]
          val req = sqliteUtils.query(url, "SELECT survey, answer1, answer2, response, s.id_survey  FROM survey_answer a " +
            "JOIN survey s WHERE a.id_survey = s.id_survey;", Seq("survey", "answer1", "answer2", "response", "id_survey"))
          println("req = " + req)
          req match {
            case Some(r) => val values1 = r.flatMap(v => to[ResponseSpeAnswer].from(v)).filter(x => x.id_survey == id_survey.toInt).groupBy(x => (x.response, x.answer1, x.answer2, x.survey))
              .map(t => (t._1._4.toString(), t._1._1 match {
                case 0 => t._1._2;
                case 1 => t._1._3
              }, t._2.length.toString.toInt))
              println("values1 = " + values1)
              complete(values1)
            //case Some(r) => val values1 = r.flatMap(v => to[SurveyAnswer].from(v))
            //  complete(values1)
            case None => complete("mauvaise table")
          }
        }
      }
    }
}
