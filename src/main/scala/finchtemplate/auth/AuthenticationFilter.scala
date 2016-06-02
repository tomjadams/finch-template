package finchtemplate.auth

import java.net.URI

import cats.data.Xor
import com.github.benhutchison.mouse.all._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Filter, Service}
import com.twitter.io.Buf
import com.twitter.util.{Future, Try}
import finchtemplate.util.hawk.HawkAuthenticate.parseRawRequestAuthHeader
import finchtemplate.util.hawk.TaggedTypesFunctions.RawAuthenticationHeader
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.HttpMethod.httpMethod
import finchtemplate.util.hawk.params._
import finchtemplate.util.hawk.validate.{Credentials, RequestAuthorisationHeader}

final class AuthenticationFilter(credentials: Credentials) extends Filter[Request, Response, Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] =
    authenticateRequest(request).fold(e => Future.exception(e), _ => service(request))

  private def authenticateRequest(request: Request): Xor[Error, RequestValid] = {

    for {
      header <- parseAuthHeader(request)
      method <- httpMethod(request.method.toString())
      requestUri <- Try(new URI(request.uri)).toOption
      host = request.host.map(Host(_)).getOrElse(Host.UnknownHost)
      port = Port(requestUri.getPort)
      path = UriPath(requestUri.getRawPath)
      rc <- new RequestContext(method, host, port, path, header, methodDependantPayloadContext(method, request.contentType, request.content))
    } yield HawkAuthenticate.authenticateRequest(credentials, rc)

  }


  private def methodDependantPayloadContext(method: HttpMethod, contentType: Option[String], content: Buf): Option[PayloadContext] =
    List(Put, Post, Patch).contains(method).option(payloadContext(contentType, content))

  private def payloadContext(contentType: Option[String], content: Buf): PayloadContext = {
    val ct = contentType.map(ContentType(_)).getOrElse(ContentType.UnknownContentType)
    PayloadContext(ct, bufToStream(content))
  }

  private def parseAuthHeader(request: Request): Option[RequestAuthorisationHeader] =
    request.headerMap.get(AuthorisationHttpHeader).flatMap(s => parseRawRequestAuthHeader(RawAuthenticationHeader(s)))


  private def bufToStream(b: Buf): Array[Byte] = {
    val output = new Array[Byte](b.length)
    b.write(output, 0)
    output
  }
}


