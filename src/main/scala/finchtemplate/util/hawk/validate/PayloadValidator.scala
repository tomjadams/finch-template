package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk.Algorithm
import finchtemplate.util.hawk.params.KeyData

case class Hash(hash: String, algorithm: Algorithm)

object PayloadValidator {
  def validatePayload(key: KeyData): Hash = ???
}
