package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._
import play.api.libs.json.{JsError, JsSuccess}

class ApplicationControllerSpec extends BaseSpecWithApplication{

  val TestApplicationController = new ApplicationController(
    component,
    repository
  )

  "ApplicationController .index()" should {
    val result = TestApplicationController.index()(FakeRequest())

    "return 200 OK response" in {
      status(result) shouldBe OK
    }
  }

  "ApplicationController .create()" should {
    val result = TestApplicationController.create()(FakeRequest())

    "return 415 unsupported media response" in {
      status(result) shouldBe Status.UNSUPPORTED_MEDIA_TYPE
    }
  }

  "ApplicationController .read()" should {
    val fakeId:String = "1234"
    val result = TestApplicationController.read(fakeId)(FakeRequest())

    "return 200 OK response with body" in {
      status(result) shouldBe Status.OK
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

}
