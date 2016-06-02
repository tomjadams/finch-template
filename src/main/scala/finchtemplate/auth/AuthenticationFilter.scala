package finchtemplate.auth

import cats.data.Xor
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.Future
import finchtemplate.auth.RequestContextBuilder.buildContext
import finchtemplate.util.hawk.HawkAuthenticate._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.validate.Credentials

final class AuthenticationFilter(credentials: Credentials) extends Filter[Request, Response, Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] =
    authenticate(request).fold(e => Future.exception(e), _ => service(request))

  private def authenticate(request: Request): Xor[Error, RequestValid] = {
    val valid: Option[Xor[Error, RequestValid]] = buildContext(request).map(context => authenticateRequest(credentials, context))
    valid.getOrElse(errorXor("bzzzt"))
  }
}


