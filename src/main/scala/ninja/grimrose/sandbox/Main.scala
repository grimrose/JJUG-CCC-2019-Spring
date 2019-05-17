package ninja.grimrose.sandbox

import java.security.SecureRandom
import java.util.Random

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.stream.{ActorMaterializer, Materializer}
import ninja.grimrose.sandbox.http.Server
import org.slf4j.bridge.SLF4JBridgeHandler
import wvlet.airframe._
import wvlet.airframe.launcher._
import wvlet.log.{LogLevel, LogSupport, Logger}

object Main {
  def main(args: Array[String]): Unit = {
    Logger.setDefaultLogLevel(LogLevel.DEBUG)

    // jul -> slf4j
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()

    Launcher.of[App].execute(args)
  }
}

class App extends LogSupport {

  @command(isDefault = true)
  def default(): Unit = {
    println("Type --help to display the list of commands")
  }

  @command(description = "start a server")
  def start(
      @option(prefix = "-p,--port", description = "port number")
      port: Int = 8080,
      @option(prefix = "--host", description = "server address")
      host: Option[String]
  ): Unit = {
    val addr = host.getOrElse("localhost")

    val system       = ActorSystem("sandbox")
    val materializer = ActorMaterializer()(system)

    val design = newDesign.withProductionMode.noLifeCycleLogging
      .bind[ActorSystem]
      .toInstance(system)
      .bind[Materializer]
      .toInstance(materializer)
      .bind[Random]
      .toInstance(new SecureRandom)

    try {
      design.build[Server] { server =>
        server.startServer(addr, port, system)
      }
    } finally {
      materializer.shutdown()

      CoordinatedShutdown(system).run(CoordinatedShutdown.jvmExitReason)
    }
  }

}
