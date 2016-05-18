package finchtemplate

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import finchtemplate.api.v1.ErrorHandler.errorHandler
import finchtemplate.api.v1.ResponseEncoders
import finchtemplate.api.v1.hello.HelloApi._
import finchtemplate.util.log.filter.RequestLoggingFilter

object FinchTemplateApi extends ResponseEncoders {
  private def api = helloApi()

  // TODO Add in service name, tracer, etc. here. using the service builder. See ToService for an implementation.
  def apiService: Service[Request, Response] = {
    val service = api.handle(errorHandler).toService
    RequestLoggingFilter.andThen(service)
  }
}
