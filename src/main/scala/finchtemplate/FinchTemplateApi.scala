package finchtemplate

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import finchtemplate.api.v1.ErrorHandler.errorHandler
import finchtemplate.api.v1.ResponseEncoders
import finchtemplate.api.v1.hello.HelloApi._
import finchtemplate.config.Config.apiAuthenticationCredentials
import finchtemplate.http.{ExceptionFilter, HawkAuthenticateRequestFilter}
import finchtemplate.util.error.ErrorResponseEncoders
import finchtemplate.util.log.filter.RequestLoggingFilter

object AuthenticationFilter extends HawkAuthenticateRequestFilter(apiAuthenticationCredentials)

object UnhandledExceptionsFilter extends ExceptionFilter[Request](ErrorResponseEncoders.exceptionResponseEncoder)

object FinchTemplateApi extends ResponseEncoders {
  private def api = helloApi()

  def apiService: Service[Request, Response] =
    UnhandledExceptionsFilter andThen
      RequestLoggingFilter andThen
      AuthenticationFilter andThen
      api.handle(errorHandler).toService
}
