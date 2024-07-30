package services

import baseSpec.BaseSpec
import connectors.LibraryConnector
import models.DataModel
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc.Results.Ok

import scala.concurrent.{ExecutionContext, Future}

class ApplicationServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{

  val mockConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new ApplicationService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "pageCount" -> 100
  )

  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      (mockConnector.get[DataModel](_: String)(_: OFormat[DataModel], _: ExecutionContext))
        .expects(url, *, *)
        .returning(Future(gameOfThrones.as[DataModel]))
        .once()

      val result: Future[Book] = testService.getGoogleBook(urlOverride = Some(url), search = "", term = "")
      whenReady(result) { book =>
        book shouldBe gameOfThrones.as[Book]
      }
    }
    "return an error" in {
      val url: String = "testUrl"

      (mockConnector.get[???](_: ???)(_: OFormat[???], _: ???))
        .expects(url, *, *)
        .returning(???)// How do we return an error?
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe ???
      }
    }
  }

}
