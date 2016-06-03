package finchtemplate.auth

import cats.data.Xor
import cats.data.Xor._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.Future
import finchtemplate.auth.RequestContextBuilder.buildContext
import finchtemplate.util.error.{AuthenticationFailedError, FinchTemplateError}
import finchtemplate.util.hawk.HawkAuthenticate._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.validate.Credentials

abstract class HawkAuthenticateRequestFilter(credentials: Credentials) extends Filter[Request, Response, Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] =
    authenticate(request).fold(e => Future.exception(e), _ => service(request))

  private def authenticate(request: Request): Xor[FinchTemplateError, RequestValid] = {
    val valid = buildContext(request).map(context => authenticateRequest(credentials, context)).getOrElse(errorXor(s"Missing authentication header '$AuthorisationHttpHeader'"))
    valid.leftMap(e => new AuthenticationFailedError("Request is not authorised", Some(e)))
  }

  def notAuthorised[T](message: String): Xor[FinchTemplateError, T] = left(new AuthenticationFailedError(message))
}


