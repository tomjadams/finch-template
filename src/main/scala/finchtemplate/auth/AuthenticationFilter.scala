package finchtemplate.auth

import cats.data.Xor
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.Future
import finchtemplate.util.hawk.HawkAuthenticate.parseRawRequestAuthHeader
import finchtemplate.util.hawk.TaggedTypesFunctions.RawAuthenticationHeader
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.HttpMethod.httpMethod
import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.hawk.validate.{Credentials, RequestAuthorisationHeader}

final class AuthenticationFilter(credentials: Credentials) extends Filter[Request, Response, Request, Response] {

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {


    val request1: Xor[Error, RequestValid] = authenticateRequest(request)
    request1.fold(e => Future.exception(e), _ => service(request))
  }

  private def authenticateRequest(request: Request): Xor[Error, RequestValid] = {
    val header = parseAuthHeader(request)
    val host = Host()
    val port = Port()
    val path = UriPath()
    val payload = Some()
    val rc = new RequestContext(httpMethod(request.method.toString()), host, port, path, header, payload)
    HawkAuthenticate.authenticateRequest(credentials, rc)
  }

  private def parseAuthHeader(request: Request): Option[RequestAuthorisationHeader] =
    request.headerMap.get(AuthorisationHttpHeader).flatMap(s => parseRawRequestAuthHeader(RawAuthenticationHeader(s)))

  //  def apply(req: HttpReq, service: Service[AuthHttpReq, HttpRep]) = {
  //    authService.auth(req) flatMap {
  //      case AuthResult(AuthResultCode.OK, Some(passport), _) => service(AuthHttpReq(req, passport))
  //    case ar: AuthResult => Future.exception(new RequestUnauthenticated(ar.resultCode))
  //    }
  //  }
}


