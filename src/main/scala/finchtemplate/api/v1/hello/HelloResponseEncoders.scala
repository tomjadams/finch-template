package finchtemplate.api.v1.hello

import com.twitter.io.Buf._
import finchtemplate.util.http.HttpOps
import io.circe.generic.auto._
import io.circe.syntax._
import io.finch.EncodeResponse

trait HelloResponseEncoders {
  implicit def helloResponseEncoder: EncodeResponse[Hello] =
    EncodeResponse(HttpOps.jsonMimeType)(hello => Utf8(("data" -> hello).asJson.noSpaces))
}
