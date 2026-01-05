package controllers

import akka.stream.Materializer
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.http.ContentTypes

class StreamControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "StreamController#streamEvents" should {
    "return Server-Sent Events content type" in {
      val controller = inject[StreamController]
      val result = controller.streamEvents().apply(FakeRequest(GET, "/api/stream/events"))

      status(result) mustBe OK
      contentType(result) mustBe Some(ContentTypes.EVENT_STREAM)
    }

    "stream events" in {
      val controller = inject[StreamController]
      val result = controller.streamEvents().apply(FakeRequest(GET, "/api/stream/events"))

      status(result) mustBe OK
      // Content is streamed, so we just verify the response is set up correctly
    }
  }

  "StreamController#streamSensorData" should {
    "return Server-Sent Events content type" in {
      val controller = inject[StreamController]
      val result = controller.streamSensorData().apply(FakeRequest(GET, "/api/stream/sensors"))

      status(result) mustBe OK
      contentType(result) mustBe Some(ContentTypes.EVENT_STREAM)
    }
  }

  "StreamController#streamNotifications" should {
    "return Server-Sent Events content type" in {
      val controller = inject[StreamController]
      val result = controller.streamNotifications().apply(FakeRequest(GET, "/api/stream/notifications"))

      status(result) mustBe OK
      contentType(result) mustBe Some(ContentTypes.EVENT_STREAM)
    }
  }

  "StreamController#streamWithBackpressure" should {
    "return Server-Sent Events content type" in {
      val controller = inject[StreamController]
      val result = controller.streamWithBackpressure().apply(FakeRequest(GET, "/api/stream/backpressure"))

      status(result) mustBe OK
      contentType(result) mustBe Some(ContentTypes.EVENT_STREAM)
    }
  }
}
