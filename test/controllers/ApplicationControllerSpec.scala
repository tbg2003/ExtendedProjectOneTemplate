package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.Result

import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication{

  val TestApplicationController = new ApplicationController(
    component,
    repository
  )
  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )


  "ApplicationController .index()" should {
    val result = TestApplicationController.index()(FakeRequest())

    "return 200 OK response" in {
      status(result) shouldBe OK
    }
  }

  "ApplicationController .create" should {

    "create a book in the database" in {

      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED
    }
  }

  "ApplicationController .read" should {

    "find a book in the database by id" in {

      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(readResult) shouldBe OK
      contentAsJson(readResult).as[DataModel] shouldBe dataModel
    }
  }

  "ApplicationController .update()" should {
    val fakeId:String = "1234"
    val result = TestApplicationController.update(fakeId)(FakeRequest())

    "return 200 OK response" in {
      status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .delete()" should {
    val fakeId:String = "1234"
    val result = TestApplicationController.delete(fakeId)(FakeRequest())

    "return 200 OK response" in {
      status(result) shouldBe Status.OK
    }
  }
  override def beforeEach(): Unit = await(repository.deleteAll())
  override def afterEach(): Unit = await(repository.deleteAll())
}
