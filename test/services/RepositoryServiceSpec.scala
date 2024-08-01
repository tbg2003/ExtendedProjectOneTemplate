package services

import baseSpec.BaseSpec
import models.{APIError, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.mvc.Results.status
import repositories.{DataRepository, MockRepository}

import scala.concurrent.{ExecutionContext, Future}


class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{
  val mockDataRepo = mock[MockRepository]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testRepoService = new RepositoryService(mockDataRepo)

  private val dataModel: DataModel = DataModel(
    "abcd",
    "isbn",
    "test name",
    Seq("test author"),
    100
  )


  "index" should {

    "return a Right" in {
      (mockDataRepo.index()(_:ExecutionContext))
        .expects(*)
        .returning(Future(Right(Seq(dataModel))))
        .once()

      whenReady(testRepoService.index()){ result =>
        result shouldBe Right(Seq(dataModel))
      }
    }
    "return a Left" in {
      val apiError: APIError.BadAPIResponse = APIError.BadAPIResponse(500, s"An error occurred")

      (mockDataRepo.index()(_:ExecutionContext))
        .expects(*)
        .returning(Future(Left(apiError)))
        .once()

      whenReady(testRepoService.index()){ result =>
        result shouldBe Left(apiError)
      }
    }
  }
}
