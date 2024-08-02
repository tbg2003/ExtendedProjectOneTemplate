package services

import baseSpec.BaseSpec
import com.mongodb.client.result.UpdateResult
import models.{APIError, DataModel}
import org.bson.{BsonType, BsonValue}
import org.mongodb.scala.bson.{BsonDocument, BsonValue}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import repositories.{DataRepository, MockRepository}

import scala.concurrent.{ExecutionContext, Future}

class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{
  val mockDataRepo = mock[MockRepository]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testRepoService = new RepositoryService(mockDataRepo)

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test subtitle",
    100
  )


  "index" should {

    "return a Right" when {
      "dataRepository .index returns a Right" in {
        (mockDataRepo.index()(_: ExecutionContext))
          .expects(*)
          .returning(Future(Right(Seq(dataModel))))
          .once()

        whenReady(testRepoService.index()) { result =>
          result shouldBe Right(Seq(dataModel))
        }
      }
    }

    "return a Left" when {
      "dataRepository .index returns a Left" in {
        val apiError: APIError.BadAPIResponse = APIError.BadAPIResponse(500, s"An error occurred")

        (mockDataRepo.index()(_: ExecutionContext))
          .expects(*)
          .returning(Future(Left(apiError)))
          .once()

        whenReady(testRepoService.index()) { result =>
          result shouldBe Left(apiError)
        }
      }
    }
  }


  "create" should {
    "return a Right" when {
      "dataRepository .create returns a Right" in {
        (mockDataRepo.create(_:DataModel))
          .expects(dataModel)
          .returning(Future(Right(dataModel)))
          .once()

        whenReady(testRepoService.create(dataModel)) { result =>
          result shouldBe Right(dataModel)
        }
      }
    }
    "return a Left" when {
      "dataRepository .create returns a Left" in {
        val apiError = APIError.BadAPIResponse(500, s"An error occurred when trying to add book with id: ${dataModel._id}")
        (mockDataRepo.create(_:DataModel))
          .expects(dataModel)
          .returning(Future(Left(apiError)))
          .once()

        whenReady(testRepoService.create(dataModel)) { result =>
          result shouldBe Left(apiError)
        }
      }
    }
  }
}
