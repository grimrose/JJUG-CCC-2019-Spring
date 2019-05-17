package ninja.grimrose.sandbox.metrics

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpHeader, HttpRequest}
import io.opencensus.contrib.http.util.HttpPropagationUtil
import io.opencensus.scala.Tracing
import io.opencensus.scala.akka.http.TracingDirective
import io.opencensus.scala.http.propagation.{B3FormatPropagation, Propagation}
import io.opencensus.trace.propagation.TextFormat
import io.opencensus.trace.{Tracing => JTracing}

object SandboxTracingDirective extends TracingDirective {
  override protected def tracing: Tracing = Tracing

  override protected def propagation: Propagation[HttpHeader, HttpRequest] =
    new B3FormatPropagation[HttpHeader, HttpRequest] {

      override protected val b3Format: TextFormat =
        if (isStackdriverTraceEnabled)
          HttpPropagationUtil.getCloudTraceFormat // X-Cloud-Trace-Context
        else JTracing.getPropagationComponent.getB3Format // X-B3-TraceId

      override def headerValue(req: HttpRequest, key: String): Option[String] =
        req.headers
          .find(_.lowercaseName() == key.toLowerCase)
          .map(_.value())

      override def createHeader(key: String, value: String): HttpHeader = RawHeader(key, value)

    }

}
