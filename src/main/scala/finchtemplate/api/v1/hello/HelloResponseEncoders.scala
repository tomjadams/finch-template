package finchtemplate.api.v1.hello

import com.twitter.finagle.http.Message
import com.twitter.finagle.http.Message.ContentTypeJson
import com.twitter.io.Buf._
import io.circe.Encoder
import io.circe.syntax._
import io.finch.EncodeResponse

trait HelloResponseEncoders {
  implicit val helloEncoder = Encoder.instance[Hello] { h =>
    Map("hello" -> Map("name" -> h.name)).asJson
  }

  implicit def helloResponseEncoder: EncodeResponse[Hello] =
    EncodeResponse(ContentTypeJson)(hello => Utf8(Map("data" -> hello).asJson.noSpaces))
}

object HelloResponseEncoders extends HelloResponseEncoders
