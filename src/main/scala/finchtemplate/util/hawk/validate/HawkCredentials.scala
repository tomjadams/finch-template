package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk._

final case class HawkCredentials(keyId: KeyId, key: Key, algorithm: Algorithm)
