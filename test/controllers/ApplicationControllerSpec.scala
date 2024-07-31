package controllers

import baseSpec.BaseSpecWithApplication
import models.{APIError, DataModel}
import org.mongodb.scala.model.Updates
import org.scalamock.scalatest.MockFactory
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.Result
import repositories.DataRepository

import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication with MockFactory{


  // TODO try mock the left/rights of DataRepository

  val TestApplicationController = new ApplicationController(
    component,
    repository,
    service
  )(executionContext)

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  "ApplicationController .index()" should {

    "return 200 OK response with body" in {
      beforeEach()

      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe OK
      afterEach()
    }
  }

  "ApplicationController .create" should {

    "return 201 Created with body" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildPost("/api/create ").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED
      contentAsJson(createdResult).as[DataModel] shouldBe dataModel
      afterEach()
    }

    "return 500 Internal Server Error" in {
      beforeEach()
      val apiError:APIError = APIError.BadAPIResponse(500, "An error has occurred:")
      val request:FakeRequest[JsValue] = buildPost("/api/create").withBody[JsValue](Json.toJson(dataModel))
      val request2:FakeRequest[JsValue] = buildPost("/api/create").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      val createdResult2: Future[Result] = TestApplicationController.create()(request2)

      status(createdResult2) shouldBe apiError.httpResponseStatus
      afterEach()
    }
    "return 400 Bad Request" in {
      beforeEach()
      val badRequestBody:JsValue = Json.parse("""{"id": "abcd", "name": 12345}""")
      val badRequest: FakeRequest[JsValue] = buildPost("/api/create").withBody[JsValue](Json.toJson(badRequestBody))
      val createdResult: Future[Result] = TestApplicationController.create()(badRequest)

      status(createdResult) shouldBe Status.BAD_REQUEST
      afterEach()
    }
  }

  "ApplicationController .read" should {

    "return 200 Ok with body" in {
      beforeEach()

      val request: FakeRequest[JsValue] = buildGet("/api/read/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[DataModel] shouldBe dataModel

      afterEach()
    }

    "return 404 Not Found with body" when {
      beforeEach()
      val request: FakeRequest[JsValue] = buildGet("/api/read/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("abc")(FakeRequest())

      status(readResult) shouldBe Status.NOT_FOUND
      contentAsJson(readResult).as[String] shouldBe "No items found with id:abc"

      afterEach()
    }
  }

  "ApplicationController .update()" should {

    "Update a book in the database by id" in {
      beforeEach()

      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val updateResult: Future[Result] = TestApplicationController.update("abcd")(request)

      status(updateResult) shouldBe Status.ACCEPTED
      contentAsJson(updateResult).as[DataModel] shouldBe dataModel

      afterEach()
    }
    "return 500 Internal Server Error" in {
      beforeEach()
      val apiError:APIError = APIError.BadAPIResponse(500, "An error has occurred:")
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val updateResult: Future[Result] = TestApplicationController.create()(request)
      val updateResult2: Future[Result] = TestApplicationController.create()(request)

      status(updateResult2) shouldBe apiError.httpResponseStatus

      afterEach()
    }

    "return 400 Bad Request" in {
      beforeEach()
      val badRequestBody:JsValue = Json.parse("""{"id": "abcd", "name": 12345}""")
      val badRequest: FakeRequest[JsValue] = buildGet("/api").withBody[JsValue](Json.toJson(badRequestBody))

      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val updateResult: Future[Result] = TestApplicationController.update("abcd")(badRequest)

      status(updateResult) shouldBe Status.BAD_REQUEST

      afterEach()
    }

  }

  "ApplicationController .delete()" should {

    "return 202 Accepted" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val deleteResult:Future[Result] = TestApplicationController.delete("abcd")(FakeRequest())

      status(deleteResult) shouldBe Status.ACCEPTED
      afterEach()
    }
  }


  override def beforeEach(): Unit = await(repository.deleteAll())
  override def afterEach(): Unit = await(repository.deleteAll())
}
