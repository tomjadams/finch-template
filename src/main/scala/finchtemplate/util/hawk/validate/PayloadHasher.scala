package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk.params.{KeyData, PayloadContext}

object PayloadHasher {
  def hash(key: KeyData, payload: PayloadContext): Hash = ???
}
