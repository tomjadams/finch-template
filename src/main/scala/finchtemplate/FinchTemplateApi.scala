package finchtemplate

import com.twitter.finagle.Service
import com.twitter.finagle.http.filter.ExceptionFilter
import com.twitter.finagle.http.{Request, Response}
import finchtemplate.api.v1.ErrorHandler.errorHandler
import finchtemplate.api.v1.ResponseEncoders
import finchtemplate.api.v1.hello.HelloApi._
import finchtemplate.config.Config.httpAuthCredentials
import finchtemplate.http.HawkAuthenticateRequestFilter
import finchtemplate.util.log.filter.RequestLoggingFilter

object AuthenticationFilter extends HawkAuthenticateRequestFilter(httpAuthCredentials)

object FinchTemplateApi extends ResponseEncoders {
  private def api = helloApi()

  def apiService: Service[Request, Response] =
    ExceptionFilter andThen
      RequestLoggingFilter andThen
      AuthenticationFilter andThen
      api.handle(errorHandler).toService
}
