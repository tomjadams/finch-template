package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk.params.{KeyData, PayloadContext}
import finchtemplate.util.hawk.validate.HashTypes.Base64Encoded

object PayloadHasher {
  def hash(key: KeyData, payload: PayloadContext): Hash = Hash(Base64Encoded("foo"), Sha512)
}
