package finchtemplate.http

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.FutureTransformer

// Modified from: https://gist.github.com/vastdevblog/2022320
final class ExceptionFilter extends SimpleFilter[Request, Response] {
  val transformer = new FutureTransformer[Response, Response] {
    override def map(value: Response): Response = value

    // An error is converted into a 500 response. The
    // Responses object is in this file.
    override def handle(throwable: Throwable): Response =
      Status.InternalServerError(throwable.getMessage)
  }

  def apply(request: Request, service: Service[Request, Response]) = service(request).transformedBy(transformer)
}
