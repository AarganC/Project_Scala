import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import akka.util.ByteString

class TipsTest extends WordSpec with Matchers with ScalatestRouteTest {
  "The service" should {
    "return all users" in {
      Get("/getAllUser") ~> tips.route ~> check {
        status.isSuccess() shouldEqual true
      }
    }
    "return all subs" in {
      Get("/getAllSubs") ~> tips.route ~> check {
        status.isSuccess() shouldEqual true
      }
    }
    "return sum donation" in {
      Get("/getSumDonationUser") ~> tips.route ~> check {
        status.isSuccess() shouldEqual true
      }
    }


    val jsonRequestAddTips = ByteString(
      s"""
         |{
         |    "id_user": "3",
         |    "somme": "55"
         |
         |}
        """.stripMargin)
    val postRequest = HttpRequest(
      HttpMethods.POST,
      uri = "/AddTips",
      entity = HttpEntity(MediaTypes.`application/json`, jsonRequestAddTips))
    "return true if addtips is OK" in {
      postRequest ~> tips.route ~> check {
        status.isSuccess() shouldEqual true
      }
    }

    val jsonRequestDelTips = ByteString(
      s"""
         |{
         |    "id_tips": "5"
         |
         |}
        """.stripMargin)
    val postRequest2 = HttpRequest(
      HttpMethods.POST,
      uri = "/DeleteTips",
      entity = HttpEntity(MediaTypes.`application/json`, jsonRequestDelTips))
    "return true if addAnswer is OK" in {
      postRequest2 ~> tips.route ~> check {
        status.isSuccess() shouldEqual true
      }
    }




  }

}
