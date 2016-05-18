package finchtemplate.util.error

import com.twitter.io.Buf._
import finchtemplate.util.http.HttpOps
import io.circe.Encoder
import io.circe.syntax._
import io.finch.EncodeResponse

trait ErrorResponseEncoders {
  implicit val exceptionEncoder = Encoder.instance[Exception] { e =>
    val base = List(
      "message" -> e.getMessage,
      "type" -> e.getClass.getSimpleName
    )
    val withCause = base.++(Option(e.getCause).map(cause => List("cause" -> cause.getMessage)).getOrElse(Nil))
    withCause.asJson
  }

  implicit def exceptionResponseEncoder: EncodeResponse[Exception] =
    EncodeResponse(HttpOps.jsonMimeType)(e => Utf8(("error" -> exceptionEncoder.apply(e)).asJson.noSpaces))
}

object ErrorResponseEncoders extends ErrorResponseEncoders
