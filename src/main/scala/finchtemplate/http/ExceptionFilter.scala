package finchtemplate.http

import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{CancelledRequestException, Service, SimpleFilter}
import com.twitter.util.{Future, NonFatal}
import finchtemplate.util.error.ErrorReporter._
import finchtemplate.util.log.Logger
import io.finch.EncodeResponse

// Copied from com.twitter.finagle.http.filter.ExceptionFilter
class ExceptionFilter[REQUEST <: Request](encoder: EncodeResponse[Exception]) extends SimpleFilter[REQUEST, Response] {
  private val log = new Logger("error")

  def apply(request: REQUEST, service: Service[REQUEST, Response]): Future[Response] = {
    val finalResponse = {
      try {
        service(request)
      } catch {
        case NonFatal(e) => Future.exception(e)
      }
    }
    finalResponse.rescue {
      case e: CancelledRequestException =>
        log.warn(s"Cancelled request, uri: ${request.uri}")
        respond(request, ClientClosedRequest, e)
      case t: Throwable =>
        try {
          errorReporter.error(t)
          log.warnST(s"Unhandled exception, uri: ${request.uri} exception: $t", t)
          respond(request, InternalServerError, t)
        } catch {
          case e: Throwable => {
            // Logging or internals are broken. Write static string to console - don't attempt to include request or exception.
            Console.err.println("ExceptionFilter failed")
            throw e
          }
        }
    }
  }

  private def respond(request: REQUEST, status: Status, t: Throwable): Future[Response] = {
    val response = request.response
    response.status = status
    response.clearContent()
    response.contentLength = 0
    Future.value(response)
  }
}
