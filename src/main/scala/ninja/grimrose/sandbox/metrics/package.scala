package ninja.grimrose.sandbox

import com.typesafe.config.{Config, ConfigFactory}

package object metrics {
  private val config: Config = ConfigFactory.load()

  val isStackdriverTraceEnabled: Boolean = config.getBoolean("opencensus-scala.trace.exporters.stackdriver.enabled")

}
