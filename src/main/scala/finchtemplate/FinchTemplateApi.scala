package finchtemplate

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import finchtemplate.api.v1.ErrorHandler.apiErrorHandler
import finchtemplate.api.v1.ResponseEncoders
import finchtemplate.api.v1.hello.HelloApi._
import finchtemplate.config.Config.apiAuthenticationCredentials
import finchtemplate.http.{ExceptionFilter, HawkAuthenticateRequestFilter, RequestLoggingFilter}
import finchtemplate.util.error.ErrorResponseEncoders

object AuthenticationFilter extends HawkAuthenticateRequestFilter(apiAuthenticationCredentials)

object UnhandledExceptionsFilter extends ExceptionFilter[Request](ErrorResponseEncoders.exceptionResponseEncoder)

object FinchTemplateApi extends ResponseEncoders {
  private def api = helloApi()

  def apiService: Service[Request, Response] =
    RequestLoggingFilter andThen
      UnhandledExceptionsFilter andThen
      AuthenticationFilter andThen
      api.handle(apiErrorHandler).toService
}
