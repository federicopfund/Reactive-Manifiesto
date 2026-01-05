package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._

class AsyncDataControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "AsyncDataController#getData" should {
    "return data as JSON" in {
      val controller = inject[AsyncDataController]
      val result = controller.getData().apply(FakeRequest(GET, "/api/data"))

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      
      val json = contentAsJson(result)
      (json \ "status").asOpt[String] must not be None
    }

    "return data with timestamp" in {
      val controller = inject[AsyncDataController]
      val result = controller.getData().apply(FakeRequest(GET, "/api/data"))

      status(result) mustBe OK
      val json = contentAsJson(result)
      (json \ "timestamp").asOpt[Long] must not be None
    }
  }

  "AsyncDataController#getCombinedData" should {
    "return combined data from multiple sources" in {
      val controller = inject[AsyncDataController]
      val result = controller.getCombinedData().apply(FakeRequest(GET, "/api/data/combined"))

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      
      val json = contentAsJson(result)
      (json \ "users").asOpt[JsArray] must not be None
      (json \ "posts").asOpt[JsArray] must not be None
      (json \ "comments").asOpt[JsArray] must not be None
      (json \ "timestamp").asOpt[Long] must not be None
    }
  }

  "AsyncDataController#getDataWithErrorHandling" should {
    "handle errors gracefully" in {
      val controller = inject[AsyncDataController]
      val result = controller.getDataWithErrorHandling().apply(FakeRequest(GET, "/api/data/with-error-handling"))

      // Should return either success or error, both are valid
      status(result) must (be(OK) or be(INTERNAL_SERVER_ERROR))
      contentType(result) mustBe Some("application/json")
      
      val json = contentAsJson(result)
      (json \ "status").asOpt[String] must not be None
    }
  }
}
