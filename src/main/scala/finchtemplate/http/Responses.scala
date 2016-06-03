package finchtemplate.http

import com.twitter.finagle.http.{Request, Response, Status}


import java.net.InetSocketAddress
import java.util.UUID
import java.util.concurrent.{Executors, TimeUnit}
import com.google.common.base.Splitter
import com.twitter.finagle.http.Http
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.service.TimeoutFilter
import com.twitter.finagle.{Service, SimpleFilter, GlobalRequestTimeoutException}
import com.twitter.util.{Future, FuturePool, FutureTransformer, Duration}
import org.codehaus.jackson.map.ObjectMapper
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.handler.codec.http.{HttpResponseStatus, DefaultHttpResponse, HttpRequest, HttpResponse}
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.handler.codec.http.HttpResponseStatus.{NOT_FOUND, INTERNAL_SERVER_ERROR}
import org.jboss.netty.util.CharsetUtil.UTF_8
import org.jboss.netty.buffer.ChannelBuffer

object Responses {

  // Used to convert objects into json
  val mapper = new ObjectMapper

  // Create an Response from a status and some content.
  private def respond(status: Status, content: ChannelBuffer): Response = {
    val response = new DefaultResponse(HTTP_1_1, status)
    response.setHeader("Content-Type", "application/json")
    response.setHeader("Cache-Control", "no-cache")
    response.setContent(content)
    response
  }

  object OK {
    def apply(req: Request, service: (Request) => Object): Response =
      respond(Status.Ok,
        copiedBuffer(mapper.writeValueAsBytes(service(req))))
  }

  object NotFound {
    def apply(): Response  =
      respond(NOT_FOUND,
        copiedBuffer("{\"status\":\"NOT_FOUND\"}", UTF_8))
  }

  object InternalServerError {
    def apply(message: String): Response =
      respond(INTERNAL_SERVER_ERROR,
        copiedBuffer("{\"status\":\"INTERNAL_SERVER_ERROR\", " +
          "\"message\":\"" + message + "\"}", UTF_8))
  }
}
