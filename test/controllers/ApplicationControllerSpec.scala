package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
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
      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe OK
    }
    "return 404 Not Found response" in {
      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe OK
    }
  }

  "ApplicationController .create" should {

    "create a book in the database" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED
      afterEach()
    }

    "handle bad request" in {
      beforeEach()
      val badRequestBody:JsValue = Json.parse("""{"id": "abcd", "name": 12345}""")
      val badRequest: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(badRequestBody))
      val createdResult: Future[Result] = TestApplicationController.create()(badRequest)

      status(createdResult) shouldBe Status.BAD_REQUEST
      afterEach()
    }
  }

  "ApplicationController .read" should {

    "find a book in the database by id" in {
      beforeEach()

      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[DataModel] shouldBe dataModel

      afterEach()
    }

    "handle error when id not in database" in {
      beforeEach()
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
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

    "Handle a bad request" in {
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

    "delete a book by the id" in {
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val deleteResult:Future[Result] = TestApplicationController.delete("abcd")(FakeRequest())

      status(deleteResult) shouldBe Status.ACCEPTED
    }
  }


  override def beforeEach(): Unit = await(repository.deleteAll())
  override def afterEach(): Unit = await(repository.deleteAll())
}
