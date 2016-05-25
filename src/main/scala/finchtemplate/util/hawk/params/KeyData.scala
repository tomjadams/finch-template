package finchtemplate.util.hawk.params

import finchtemplate.util.hawk._
import finchtemplate.util.hawk.validate.Algorithm

case class KeyData(keyId: KeyId, key: Key, algorithm: Algorithm)
