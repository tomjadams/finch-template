package finchtemplate.http

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{CancelledRequestException, Service, SimpleFilter}
import com.twitter.util.{Future, NonFatal}
import finchtemplate.util.log.Logger

// Copied from com.twitter.finagle.http.filter.ExceptionFilter
class ExceptionFilter[REQUEST <: Request] extends SimpleFilter[REQUEST, Response] {
  private val log = new Logger("error")

  // TODO TJA Add in rollbar/etc. exception handling.

  def apply(request: REQUEST, service: Service[REQUEST, Response]): Future[Response] = {
    val serviceResponse = {
      try {
        service(request)
      } catch {
        case NonFatal(e) => Future.exception(e)
      }
    }
    serviceResponse.rescue {
      case e: CancelledRequestException =>
        // This only happens when ChannelService cancels a reply.
        log.warn(s"Cancelled request, uri: ${request.uri}")
        respond(request, Status.ClientClosedRequest)
      case e: Throwable =>
        try {
          log.warnST(s"Unhandled exception, uri: ${request.uri} exception: $e", e)
          respond(request, Status.InternalServerError)
        } catch {
          case e: Throwable => {
            // Logging or internals are broken. Write static string to console - don't attempt to include request or exception.
            Console.err.println("ExceptionFilter failed")
            throw e
          }
        }
    }
  }

  private def respond(request: REQUEST, responseStatus: Status): Future[Response] = {
    val response = request.response
    response.status = responseStatus
    response.clearContent()
    response.contentLength = 0
    Future.value(response)
  }
}

object ExceptionFilter extends ExceptionFilter[Request]
