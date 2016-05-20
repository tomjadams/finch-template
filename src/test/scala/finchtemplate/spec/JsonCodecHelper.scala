package finchtemplate.spec

import io.finch.EncodeResponse

trait JsonCodecHelper {
  def toResponseString[A: EncodeResponse](a: A)(implicit encoder: EncodeResponse[A]): String = {
    val buff = encoder(a)
    val output = new Array[Byte](buff.length)
    buff.write(output, 0)
    new String(output)
  }
}
