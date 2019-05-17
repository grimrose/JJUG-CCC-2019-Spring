package ninja.grimrose.sandbox.http

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.HttpApp
import io.opencensus.exporter.stats.prometheus.PrometheusStatsCollector
import io.opencensus.exporter.stats.stackdriver.StackdriverStatsExporter
import io.prometheus.client.hotspot.DefaultExports
import wvlet.log.LogSupport

import scala.concurrent.{ExecutionContext, Future, Promise}

class Server extends HttpApp with Routes with LogSupport {

  import ninja.grimrose.sandbox.metrics._

  override protected def postHttpBinding(binding: Http.ServerBinding): Unit = {
    super.postHttpBinding(binding)

    DefaultExports.initialize()
    PrometheusStatsCollector.createAndRegister()

    if (isStackdriverTraceEnabled) {
      info("start Stackdriver stats exporter")
      StackdriverStatsExporter.createAndRegister()
    }
  }

  override protected def waitForShutdownSignal(system: ActorSystem)(implicit ec: ExecutionContext): Future[Done] = {
    val promise = Promise[Done]()
    sys.addShutdownHook {
      promise.trySuccess(Done)
    }

    // not need to press RETURN key

    promise.future
  }
}
