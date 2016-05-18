package finchtemplate.util.http

import java.net.URL

import com.twitter.conversions.time._
import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Method, Request, RequestBuilder, Response, Http => HttpCodec}
import com.twitter.io.Buf
import com.twitter.util.Future
import finchtemplate.util.error.Errors._
import finchtemplate.util.error.FinchTemplateError
import org.jboss.netty.handler.codec.http.HttpHeaders.Names.USER_AGENT

trait HttpClient {
  type HttpClientResponse = Future[Either[FinchTemplateError, Response]]
  type HttpStatusCode = Int

  val userAgent = "Finch Template HTTP Client"
  val concurrentConnections = 10
  val retries = 3
  val maxRedirects = 5

  def execute(method: Method, url: URL, content: Option[Buf]): HttpClientResponse = execute(0, method, url, content)

  private def execute(redirects: Int, method: Method, url: URL, content: Option[Buf]): HttpClientResponse = {
    val client = buildClient(url)
    val request = buildRequest(method, url, content)
    val resp = client(request).flatMap { response =>
      response.statusCode match {
        case code if (300 until 400).contains(code) => redirect(redirects, response.location, method, content)
        case code if code == 400 => Future.value(Left(upstreamClientError(response)))
        case code if code == 401 => Future.value(Left(upstreamAuthenticationError(response)))
        case code if (401 until 500).contains(code) => Future.value(Left(upstreamClientError(response)))
        case code if code >= 500 => Future.value(Left(upstreamServerError(response)))
        case _ => Future.value(Right(response))
      }
    }
    resp.ensure(client.close())
  }

  private def redirect(redirects: Int, newLocation: Option[String], method: Method, content: Option[Buf]): HttpClientResponse = {
    newLocation.map(l => {
      if (redirects < maxRedirects) {
        execute(redirects + 1, method, new URL(l), content)
      } else {
        Future.exception(tooManyRedirects(redirects))
      }
    }).getOrElse(Future.exception(error("No location header found in redirect")))
  }

  // See also:
  // * http://twitter.github.io/finagle/guide/FAQ.html#configuring-finagle6
  // * https://twitter.github.io/finagle/docs/index.html#com.twitter.finagle.builder.ClientBuilder
  // * https://twitter.github.io/finagle/guide/Clients.html#client-modules
  private def buildClient(url: URL): Service[Request, Response] = {
    ClientBuilder()
      .codec(HttpCodec())
      .hosts(s"${url.getHost}:${url.getPort}")
      .tls(url.getHost)
      .hostConnectionLimit(concurrentConnections)
      .tcpConnectTimeout(1.seconds)
      .retries(retries)
      //.reportTo(new OstrichStatsReceiver)
      .build()
  }

  private def buildRequest(method: Method, url: URL, content: Option[Buf]): Request =
    RequestBuilder().setHeader(USER_AGENT, userAgent).url(url).build(method, content)
}

object HttpClient extends HttpClient
