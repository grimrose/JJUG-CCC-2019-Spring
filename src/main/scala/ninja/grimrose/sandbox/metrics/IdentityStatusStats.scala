package ninja.grimrose.sandbox.metrics

import io.opencensus.scala.Stats

trait IdentityStatusStats {

  import io.opencensus.scala.stats._

  private val KEY_STATUS = "identity_status"

  private val measure = Measure.long("status", "calls count", "1").get

  View("sandbox/identity/calls", "The number of calls", measure, List(KEY_STATUS), Count).foreach(Stats.registerView)

  def recordOk(): Unit = Tag(KEY_STATUS, "OK").foreach(tag => Stats.record(List(tag), Measurement.long(measure, 1)))

  def recordError(): Unit =
    Tag(KEY_STATUS, "ERROR").foreach(tag => Stats.record(List(tag), Measurement.long(measure, 1)))

}
