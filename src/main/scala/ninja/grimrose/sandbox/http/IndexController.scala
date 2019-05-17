package ninja.grimrose.sandbox.http

import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.{Directives, Route}
import de.heikoseeberger.akkahttpjackson.JacksonSupport
import io.opencensus.scala.Tracing
import io.opencensus.trace.AttributeValue
import ninja.grimrose.sandbox.core.LoadGitStatus
import ninja.grimrose.sandbox.metrics.SandboxTracingDirective.traceRequest
import wvlet.airframe._

import scala.concurrent.{ExecutionContext, Future}

trait IndexController extends Directives with JacksonSupport {

  private implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  private val useCase = bind[LoadGitStatus]

  def route(): Route = pathEndOrSingleSlash {
    get {
      traceRequest { span =>
        extractLog { log =>
          extractActorSystem { system =>
            implicit val dispatcher: ExecutionContext = system.dispatchers.lookup("sandbox-blocking-io-dispatcher")

            val future = Tracing.traceWithParent("git-status", span) { childSpan =>
              Future {
                val status = useCase.handle()

                childSpan.putAttribute(
                  "git.commit.id",
                  AttributeValue.stringAttributeValue(status.getCommitId)
                )

                status
              }
            }

            onSuccess(future) { status =>
              log.info(s"git.commit.id -> ${status.getCommitId}")

              complete(status.value)
            }
          }
        }
      }
    }
  }

}
