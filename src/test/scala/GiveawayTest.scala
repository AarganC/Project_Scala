import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import akka.util.ByteString

class GiveawayTest extends WordSpec with Matchers with ScalatestRouteTest {
  "The service" should {
    val jsonRequestAddSurvey = ByteString(
      s"""
         |{
         |    "giveaway": "test",
         |	  "id_user": "1"
         |}
        """.stripMargin)
    val postRequest = HttpRequest(
      HttpMethods.POST,
      uri = "/addGiveaway",
      entity = HttpEntity(MediaTypes.`application/json`, jsonRequestAddSurvey))
    "return true if addGiveaway is OK" in {
      postRequest ~> giveaway.route_addGiveaway ~> check {
        status.isSuccess() shouldEqual true
      }
    }
  }

  "The service 2" should {
    val jsonRequestAddSurvey = ByteString(
      s"""
         |{
         |    "id_user": "1",
         |	  "id_giveaway": "2"
         |}
        """.stripMargin)
    val postRequest = HttpRequest(
      HttpMethods.POST,
      uri = "/subscribeGiveaway",
      entity = HttpEntity(MediaTypes.`application/json`, jsonRequestAddSurvey))
    "return true if subscribeGiveaway is OK" in {
      postRequest ~> giveaway.route_subscribeGiveaway ~> check {
        status.isSuccess() shouldEqual true
      }
    }
  }
}
