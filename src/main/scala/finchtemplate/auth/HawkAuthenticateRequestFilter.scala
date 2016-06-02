package finchtemplate.auth

import cats.data.Xor
import cats.data.Xor._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.Future
import finchtemplate.auth.RequestContextBuilder.buildContext
import finchtemplate.util.error.AuthenticationFailedError
import finchtemplate.util.hawk.HawkAuthenticate._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.validate.Credentials

abstract class HawkAuthenticateRequestFilter(credentials: Credentials) extends Filter[Request, Response, Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] =
    authenticate(request).fold(e => Future.exception(e), _ => service(request))

  // TODO TJA We probably want to return a 401 here (rather than a straight error), with the HMAC details in it. Perhaps map across all errors
  private def authenticate(request: Request): Xor[Error, RequestValid] = {
    val valid: Option[Xor[Error, RequestValid]] = buildContext(request).map(context => authenticateRequest(credentials, context))
    valid.getOrElse(unauthError(s"Missing authentication header '$AuthorisationHttpHeader'"))
  }

  def unauthError[T](message: String): Xor[Error, T] = left(new AuthenticationFailedError(message))
}


