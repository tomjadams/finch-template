package finchtemplate.util.error

import com.twitter.io.Buf._
import finchtemplate.util.http.HttpOps
import io.circe.Encoder
import io.circe.syntax._
import io.finch.EncodeResponse

trait ErrorResponseEncoders {
  implicit val exceptionEncoder = Encoder.instance[Exception] { e =>
    val base = Map(
      "message" -> e.getMessage,
      "type" -> e.getClass.getSimpleName
    )
    val withCause = base.++(Option(e.getCause).map(cause => Map("cause" -> cause.getMessage)).getOrElse(Map()))
    withCause.asJson
  }

  implicit def exceptionResponseEncoder: EncodeResponse[Exception] =
    EncodeResponse(HttpOps.jsonMimeType)(e => Utf8(Map("error" -> exceptionEncoder.apply(e)).asJson.noSpaces))
}

object ErrorResponseEncoders extends ErrorResponseEncoders
