package ninja.grimrose.sandbox.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import wvlet.airframe._

trait Routes extends Directives {

  private val index = bind[IndexController]

  private val identity = bind[IdentityController]

  private val metrics = bind[MetricsController]

  def routes: Route = logRequest("sandbox") {

    index.route() ~ identity.route() ~ metrics.route() ~ path("liveness_check") {
      get {
        complete(StatusCodes.OK)
      }
    } ~ path("readiness_check") {
      get {
        complete(StatusCodes.OK)
      }
    }
  }

}
