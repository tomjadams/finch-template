package finchtemplate.http

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.{Future, NonFatal}
import finchtemplate.api.v1.ErrorHandler
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
    finalResponse.rescue(ErrorHandler.topLevelErrorHandler(request, encoder))
  }
}
