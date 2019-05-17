package ninja.grimrose.sandbox.http

import java.io.StringWriter

import akka.http.scaladsl.model.{ContentType, HttpCharsets, HttpEntity, MediaTypes}
import akka.http.scaladsl.server.{Directives, Route}
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

import scala.concurrent.Future

trait MetricsController extends Directives {

  private val contentType: ContentType =
    MediaTypes.`text/plain`
      .withParams(Map("version" -> "0.0.4"))
      .withCharset(HttpCharsets.`UTF-8`)

  def route(): Route = path("metrics") {
    get {
      extractExecutionContext { implicit ec =>
        val future = Future {
          val writer = new StringWriter()

          try {
            TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples())

            writer.toString
          } finally {
            writer.close()
          }
        }

        onSuccess(future) { stats =>
          complete(HttpEntity(stats).withContentType(contentType))
        }
      }
    }
  }

}
