package finchtemplate.api.v1

import com.twitter.finagle.CancelledRequestException
import com.twitter.finagle.http.Status.{BadRequest => _, InternalServerError => _, NotFound => _, Unauthorized => _}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import finchtemplate.http.ResponseOps
import finchtemplate.util.error.ErrorReporter._
import finchtemplate.util.error.{AuthenticationFailedError, NotFoundError}
import finchtemplate.util.log.Logger._
import io.finch.Error._
import io.finch._

trait ErrorHandler extends ResponseOps {
  def apiErrorHandler: PartialFunction[Throwable, Output[Nothing]] = {
    case e: NotPresent => BadRequest(e)
    case e: NotParsed => BadRequest(e)
    case e: NotValid => BadRequest(e)
    case e: RequestErrors => BadRequest(e)
    case e: AuthenticationFailedError => Unauthorized(e)
    case e: NotFoundError => NotFound(e)
    case e: Exception => InternalServerError(e)
  }

  def filterErrorHandler[REQUEST <: Request](request: REQUEST, encoder: EncodeResponse[Throwable]): PartialFunction[Throwable, Future[Response]] = {
    case e: AuthenticationFailedError => respond(request, Status.Unauthorized, e, encoder)
    case e: CancelledRequestException => respond(request, Status.ClientClosedRequest, e, encoder)
    case t: Throwable => unhandledException(request, t, encoder)
  }

  private def unhandledException[REQUEST <: Request](request: REQUEST, t: Throwable, encoder: EncodeResponse[Throwable]): Future[Response] = {
    try {
      log.info(s"Unhandled exception on URI ${request.uri} with message $t")
      errorReporter.error(t)
      respond(request, Status.InternalServerError, t, encoder)
    } catch {
      case e: Throwable => {
        Console.err.println(s"Unable to log unhandled exception: $e")
        throw e
      }
    }
  }

  private def respond[REQUEST <: Request](request: REQUEST, status: Status, t: Throwable, encoder: EncodeResponse[Throwable]): Future[Response] = {
    val response = jsonResponse(request, status, t)(encoder)
    response.cacheControl = "no-cache"
    Future.value(response)
  }
}

object ErrorHandler extends ErrorHandler
