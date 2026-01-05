package controllers

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.libs.json._
import play.api.mvc._

import javax.inject._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class StreamController @Inject()(
  cc: ControllerComponents,
  actorSystem: ActorSystem
)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /**
   * Endpoint reactivo que emite eventos en tiempo real usando Server-Sent Events (SSE)
   * Demuestra: Akka Streams, backpressure, streaming de datos
   */
  def streamEvents: Action[AnyContent] = Action {
    // Crea un Source que emite eventos cada segundo
    val eventSource: Source[JsValue, NotUsed] = Source
      .tick(0.seconds, 1.second, ())
      .map { _ =>
        Json.obj(
          "type" -> "update",
          "timestamp" -> System.currentTimeMillis(),
          "data" -> Json.obj(
            "counter" -> System.currentTimeMillis() % 1000,
            "status" -> "active"
          )
        )
      }
      .take(30) // Limita a 30 eventos para evitar streams infinitos

    // Convierte el Source a Server-Sent Events
    Ok.chunked(eventSource via EventSource.flow).as(ContentTypes.EVENT_STREAM)
  }

  /**
   * Endpoint reactivo que simula un stream de datos de sensores
   * Demuestra: Transformaciones de streams, throttling
   */
  def streamSensorData: Action[AnyContent] = Action {
    val sensorSource: Source[JsValue, NotUsed] = Source
      .tick(0.seconds, 500.millis, ())
      .map { _ =>
        val random = scala.util.Random
        Json.obj(
          "type" -> "sensor_reading",
          "timestamp" -> System.currentTimeMillis(),
          "data" -> Json.obj(
            "temperature" -> (20.0 + random.nextDouble() * 10.0),
            "humidity" -> (40.0 + random.nextDouble() * 30.0),
            "pressure" -> (1000.0 + random.nextDouble() * 50.0)
          )
        )
      }
      .take(60) // 30 segundos de datos

    Ok.chunked(sensorSource via EventSource.flow).as(ContentTypes.EVENT_STREAM)
  }

  /**
   * Endpoint reactivo que simula notificaciones en tiempo real
   * Demuestra: Streams con diferentes tipos de eventos, filtrado
   */
  def streamNotifications: Action[AnyContent] = Action {
    val notificationTypes = List("info", "warning", "error", "success")
    
    val notificationSource: Source[JsValue, NotUsed] = Source
      .tick(0.seconds, 2.seconds, ())
      .map { _ =>
        val random = scala.util.Random
        val notifType = notificationTypes(random.nextInt(notificationTypes.length))
        Json.obj(
          "type" -> "notification",
          "level" -> notifType,
          "timestamp" -> System.currentTimeMillis(),
          "message" -> s"Reactive notification: $notifType at ${System.currentTimeMillis()}",
          "id" -> java.util.UUID.randomUUID().toString
        )
      }
      .take(20) // 40 segundos de notificaciones

    Ok.chunked(notificationSource via EventSource.flow).as(ContentTypes.EVENT_STREAM)
  }

  /**
   * Endpoint reactivo que demuestra backpressure con buffering
   * Demuestra: Buffer management, flow control
   */
  def streamWithBackpressure: Action[AnyContent] = Action {
    val fastSource: Source[JsValue, NotUsed] = Source
      .tick(0.millis, 100.millis, ())
      .map { _ =>
        Json.obj(
          "type" -> "fast_event",
          "timestamp" -> System.currentTimeMillis(),
          "value" -> scala.util.Random.nextInt(1000)
        )
      }
      .buffer(10, akka.stream.OverflowStrategy.dropHead) // Buffer con estrategia de backpressure
      .throttle(1, 1.second) // Limita a 1 evento por segundo
      .take(15)

    Ok.chunked(fastSource via EventSource.flow).as(ContentTypes.EVENT_STREAM)
  }
}
