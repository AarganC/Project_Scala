import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import akka.util.ByteString

class SurveyTest extends WordSpec with Matchers with ScalatestRouteTest {
  "The service" should {
    "return a message Database is ready" in {
      Get("/startDB") ~> survey.route_startDb ~> check {
        responseAs[String] shouldEqual "Database is ready"
      }
    }

    val jsonRequestAddSurvey = ByteString(
      s"""
         |{
         |    "survey": "test",
         |    "answer1": "aaaaa",
         |    "answer2": "zzzzzz"
         |}
        """.stripMargin)
    val postRequest = HttpRequest(
      HttpMethods.POST,
      uri = "/addSurvey",
      entity = HttpEntity(MediaTypes.`application/json`, jsonRequestAddSurvey))
    "return true if addSurvey is OK" in {
      postRequest ~> survey.route_addSurvey ~> check {
        status.isSuccess() shouldEqual true
      }
    }

    val jsonRequestAddAnswer = ByteString(
      s"""
         |{
         |    "id_survey": "56789",
         |    "id_user": "98210",
         |    "response": "0"
         |}
        """.stripMargin)
    val postRequest2 = HttpRequest(
      HttpMethods.POST,
      uri = "/addAnswer",
      entity = HttpEntity(MediaTypes.`application/json`, jsonRequestAddAnswer))
    "return true if addAnswer is OK" in {
      postRequest2 ~> survey.route_addAnswer ~> check {
        status.isSuccess() shouldEqual true
      }
    }

    // Je n'ai pas réussi à la tester car elle retourne une reponse au format json qui est amener à changer
    /*
    "return a message json message" in {
      Get("/getResume") ~> survey.route_startDb ~> check {
        status.isSuccess() shouldEqual true
      }
    }
    */

    val jsonRequestResume = ByteString(
      s"""
         |{
         |    "id_survey": "987654321"
         |}
        """.stripMargin)
    val postRequest3 = HttpRequest(
      HttpMethods.POST,
      uri = "/getSpeResume",
      entity = HttpEntity(MediaTypes.`application/json`, jsonRequestResume))
    "return true if getSpeResume is OK" in {
      postRequest3 ~> survey.route_getSpeResume ~> check {
        status.isSuccess() shouldEqual true
      }
    }
  }
}
