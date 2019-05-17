package ninja.grimrose.sandbox.http

import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.{RestartSource, Sink, Source}
import de.heikoseeberger.akkahttpjackson.JacksonSupport
import io.opencensus.scala.Tracing
import io.opencensus.trace.{AttributeValue, Span}
import ninja.grimrose.sandbox.core.GenerateMessageId
import ninja.grimrose.sandbox.metrics.IdentityStatusStats
import wvlet.airframe._
import wvlet.log.LogSupport

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait IdentityController extends Directives with IdentityStatusStats with JacksonSupport with LogSupport {

  import ninja.grimrose.sandbox.metrics.SandboxTracingDirective._

  private implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  private val useCase = bind[GenerateMessageId]

  def route(): Route = pathPrefix("identity") {
    traceRequest { span =>
      extractLog { log =>
        extractActorSystem { system =>
          implicit val ec: ExecutionContext = system.dispatchers.lookup("sandbox-fork-join-dispatcher")

          concat(
            pathPrefix("gen1") {
              get {
                onComplete(generateWithTracing(span)) {
                  case Success(id) =>
                    recordOk()
                    complete(Map("id" -> id.value))
                  case Failure(e) =>
                    log.error(e, e.getMessage)
                    recordError()
                    complete(StatusCodes.InternalServerError -> e.getMessage)
                }
              }
            },
            pathPrefix("gen2") {
              get {
                extractMaterializer { implicit mat =>
                  onComplete(generateWithBackoff(span).runWith(Sink.head)) {
                    case Success(id) =>
                      recordOk()
                      complete(Map("id" -> id.value))
                    case Failure(e) =>
                      log.error(e, e.getMessage)
                      recordError()
                      complete(StatusCodes.InternalServerError -> e.getMessage)
                  }
                }
              }
            }
          )
        }
      }
    }
  }

  import scala.concurrent.duration._

  private def generateWithBackoff(parentSpan: Span)(implicit ec: ExecutionContext) =
    RestartSource.onFailuresWithBackoff(
      minBackoff = 10.milliseconds,
      maxBackoff = 1.second,
      randomFactor = 2.0,
      maxRestarts = 3
    ) { () =>
      Source
        .single(parentSpan)
        .mapAsync(1) { span =>
          generateWithTracing(span)
        }
    }

  private def generateWithTracing(parentSpan: Span)(implicit ec: ExecutionContext) =
    Tracing.traceWithParent("generate", parentSpan) { span =>
      useCase.handle() match {
        case Left(value) =>
          span.putAttribute("generate.status", AttributeValue.booleanAttributeValue(false))

          Future.failed(new IllegalArgumentException(s"number is $value"))

        case Right(id) =>
          span.putAttribute("generate.status", AttributeValue.booleanAttributeValue(true))

          Future.successful(id)
      }
    }

}
