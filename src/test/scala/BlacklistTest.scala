import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import akka.util.ByteString

class BlacklistTest extends WordSpec with Matchers with ScalatestRouteTest {
  "The service" should {
    val jsonRequestAddSurvey = ByteString(
      s"""
         |{
         |    "id_user": "2"
         |}
        """.stripMargin)
    val postRequest = HttpRequest(
      HttpMethods.PUT,
      uri = "/blacklistUser",
      entity = HttpEntity(MediaTypes.`application/json`, jsonRequestAddSurvey))
    "return true if blacklistUser is OK" in {
      postRequest ~> blacklist.route_blacklistUser ~> check {
        status.isSuccess() shouldEqual true
      }
    }
  }
}
