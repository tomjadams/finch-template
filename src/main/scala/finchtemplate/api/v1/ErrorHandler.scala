package finchtemplate.api.v1

import finchtemplate.util.error.{AuthenticationFailedError, NotFoundError}
import io.finch.Error._
import io.finch._

object ErrorHandler {
  def errorHandler: PartialFunction[Throwable, Output[Nothing]] = {
    case e: NotPresent => BadRequest(e)
    case e: NotParsed => BadRequest(e)
    case e: NotValid => BadRequest(e)
    case e: RequestErrors => BadRequest(e)
    case e: AuthenticationFailedError => Unauthorized(e)
    case e: NotFoundError => NotFound(e)
    case e: Exception => InternalServerError(e)
  }
}
