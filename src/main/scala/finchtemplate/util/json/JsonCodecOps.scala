package finchtemplate.util.json

import java.nio.charset.StandardCharsets._

import io.finch.EncodeResponse

trait JsonCodecOps {
  def jsonString[A: EncodeResponse](a: A)(implicit encoder: EncodeResponse[A]): String = {
    val buff = encoder.apply(a)
    val output = new Array[Byte](buff.length)
    buff.write(output, 0)
    new String(output, UTF_8)
  }
}

object JsonCodecOps extends JsonCodecOps
