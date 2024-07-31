package services

import baseSpec.BaseSpec
import cats.data.EitherT
import models.{APIError, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json}
import repositories.DataRepository

import scala.concurrent.{ExecutionContext, Future}

class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{

  val mockRepository: DataRepository = mock[DataRepository]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new RepositoryService(mockRepository)

  val jsObject: JsValue = Json.obj(
    "_id" -> "someId",
    "name" -> "creative name",
    "description" -> "cool description",
    "pageCount" -> 100
  )
  val dataObject:DataModel = DataModel(_id = "someId", name = "creative name", description = "cool description", pageCount = 100)

  "index" should {
    "return a list of books" in {
      (mockRepository.index()(_:ExecutionContext))
        .expects(executionContext)
        .returning(Future(Right(Seq(dataObject))))
        .once()

      whenReady(testService.index()){ result =>
        result shouldBe Right(dataObject)
      }
    }
  }

}
