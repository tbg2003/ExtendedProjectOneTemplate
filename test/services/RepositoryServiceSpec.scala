package services

import baseSpec.BaseSpec
import models.{APIError, DataModel}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json}
import repositories.DataRepository
import scala.concurrent.{ExecutionContext, Future}


class RepositoryServiceSpec extends BaseSpec with TestMocks with ScalaFutures with GuiceOneAppPerSuite{


  val mockRepository: DataRepository = mock[DataRepository]
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
        .expects(*)
        .returning(Future.successful(Right(Seq(dataObject))))
        .once()

      whenReady(testService.index()){ result =>
        result shouldBe Right(dataObject)
      }
    }
  }

}
