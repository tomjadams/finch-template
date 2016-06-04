package finchtemplate.http

import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{CancelledRequestException, Service, SimpleFilter}
import com.twitter.util.{Future, NonFatal}
import finchtemplate.util.error.AuthenticationFailedError
import finchtemplate.util.error.ErrorReporter._
import finchtemplate.util.error.ErrorResponseEncoders.exceptionResponseEncoder
import finchtemplate.util.json.JsonCodecOps._
import finchtemplate.util.log.Logger.log
import io.finch.EncodeResponse

// Copied from com.twitter.finagle.http.filter.ExceptionFilter
class ExceptionFilter[REQUEST <: Request](encoder: EncodeResponse[Throwable]) extends SimpleFilter[REQUEST, Response] {
  def apply(request: REQUEST, service: Service[REQUEST, Response]): Future[Response] = {
    val finalResponse = {
      try {
        service(request)
      } catch {
        case NonFatal(e) => Future.exception(e)
      }
    }
    finalResponse.rescue {
      case e: AuthenticationFailedError =>
        respond(request, Unauthorized, e)
      case e: CancelledRequestException =>
        log.info(s"Client cancelled request to URI ${request.uri} with message ${e}")
        respond(request, ClientClosedRequest, e)
      case t: Throwable =>
        try {
          log.info(s"Unhandled exception on URI ${request.uri} with message ${t}")
          errorReporter.error(t)
          respond(request, InternalServerError, t)
        } catch {
          case e: Throwable => {
            Console.err.println("Unable to log unhandled exception")
            throw e
          }
        }
    }
  }

  private def rescueException[A](t: Exception): PartialFunction[Throwable, Future[A]] = {
    ???
  }

  private def respond(request: REQUEST, status: Status, t: Throwable): Future[Response] = {
    val response: Response = request.response
    response.status = status
    response.cacheControl = "no-cache"
    response.setContentTypeJson()
    response.contentString = jsonString(t)
    Future.value(response)
  }
}
