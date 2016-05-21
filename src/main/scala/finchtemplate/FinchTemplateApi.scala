package finchtemplate

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import finchtemplate.api.v1.ErrorHandler.errorHandler
import finchtemplate.api.v1.ResponseEncoders
import finchtemplate.api.v1.hello.HelloApi._
import finchtemplate.util.log.filter.RequestLoggingFilter

object FinchTemplateApi extends ResponseEncoders {
  private def api = helloApi()

  def apiService: Service[Request, Response] = {
    val service = api.handle(errorHandler).toService
    RequestLoggingFilter.andThen(service)
  }
}
