package finchtemplate.http

import com.twitter.finagle.http.Status._
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{CancelledRequestException, Service, SimpleFilter}
import com.twitter.util.{Future, NonFatal}
import finchtemplate.util.error.ErrorReporter._
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
      case e: CancelledRequestException =>
        respond(request, ClientClosedRequest, e)
      case t: Throwable =>
        try {
          errorReporter.error(t)
          respond(request, InternalServerError, t)
        } catch {
          case e: Throwable => {
            Console.err.println("ExceptionFilter failed")
            throw e
          }
        }
    }
  }

  private def respond(request: REQUEST, status: Status, t: Throwable): Future[Response] = {

    //encoder.apply(t)

    val response = request.response
    response.status = status
    response.clearContent()
    response.contentLength = 0
    Future.value(response)
  }
}
