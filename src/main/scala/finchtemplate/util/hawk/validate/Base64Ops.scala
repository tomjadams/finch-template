package finchtemplate.util.hawk.validate

import java.util.Base64

import finchtemplate.util.hawk.TaggedTypesFunctions.Base64Encoded
import finchtemplate.util.hawk._

trait Base64Ops {
  def base64Encode(data: Array[Byte]): Base64Encoded = Base64Encoded(Base64.getEncoder.encodeToString(data))
}

object Base64Ops extends Base64Ops

