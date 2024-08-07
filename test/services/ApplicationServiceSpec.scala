package services

import baseSpec.BaseSpec
import cats.data.EitherT
import connectors.LibraryConnector
import models.APIError
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json, OFormat}
import models.GoogleBook._
import scala.concurrent.{ExecutionContext, Future}

class ApplicationServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite {

  val mockConnector: LibraryConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new ApplicationService(mockConnector)

  val googleBooksResponse: JsValue = Json.obj(
    "kind" -> "books#volumes",
    "totalItems" -> 1,
    "items" -> Json.arr(
      Json.obj(
        "kind" -> "books#volume",
        "id" -> "someId",
        "volumeInfo" -> Json.obj(
          "title" -> "A Game of Thrones",
          "subtitle" -> "subtitle",
          "pageCount" -> 100,
          "industryIdentifiers" -> Json.arr(
            Json.obj(
              "type" -> "ISBN_13",
              "identifier" -> "9780553103540"
            )
          )
        )
      )
    )
  )

  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      (mockConnector.get[GoogleBooksResponse](_: String)(_: OFormat[GoogleBooksResponse], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.rightT[Future, APIError](googleBooksResponse.as[GoogleBooksResponse]))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Right(Book(
          isbn = "9780553103540",
          title = "A Game of Thrones",
          subtitle = "subtitle",
          pageCount = 100
        ))
      }
    }

    "return an error" in {
      val apiError: APIError.BadAPIResponse = APIError.BadAPIResponse(500, "Could not connect")
      val url: String = "testUrl"

      (mockConnector.get[GoogleBooksResponse](_: String)(_: OFormat[GoogleBooksResponse], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.leftT[Future, GoogleBooksResponse](apiError))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
        result shouldBe Left(apiError)
      }
    }
  }
}
