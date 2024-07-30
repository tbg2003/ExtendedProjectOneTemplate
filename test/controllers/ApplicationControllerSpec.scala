package controllers

import baseSpec.BaseSpecWithApplication
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.test.Helpers._


class ApplicationControllerSpec extends BaseSpecWithApplication{
  val TestApplicationController = new ApplicationController(
    component
  )

  "ApplicationController .index()" should {
    val result = TestApplicationController.index()(FakeRequest())

    "return 200 OK response" in {
      status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .create()" should {
    val result = TestApplicationController.create()(FakeRequest())

    "return 200 OK response" in {
      status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .read()" should {
    val fakeId:String = "1234"
    val result = TestApplicationController.read(fakeId)(FakeRequest())

    "return 200 OK response" in {
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
