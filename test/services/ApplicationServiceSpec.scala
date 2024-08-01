package services

import baseSpec.BaseSpec
import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}


import scala.concurrent.{ExecutionContext, Future}

class ApplicationServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{

  val mockConnector: LibraryConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new ApplicationService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "_id" -> "someId",
    "isbn" -> "someISBN",
    "title" -> "A Game of Thrones",
    "authors" -> Seq("Author1"),
    "pageCount" -> 100
  )

  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      (mockConnector.get[DataModel](_: String)(_: OFormat[DataModel], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.rightT[Future, APIError](gameOfThrones.as[DataModel]))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Right(gameOfThrones.as[Book])
      }
    }

    "return an error" in {
      val apiError: APIError.BadAPIResponse = APIError.BadAPIResponse(500, "Could not connect")
      val url: String = "testUrl"

    (mockConnector.get[DataModel](_: String)(_: OFormat[DataModel], _: ExecutionContext))
      .expects(url, *, *)
      .returning(EitherT.leftT[Future, DataModel](apiError))
      .once()

    whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
      result shouldBe Left(apiError)
    }
  }
}

}
