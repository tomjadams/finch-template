package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk._
import finchtemplate.util.time.Seconds

final case class RequestAuthorisationHeader(keyId: KeyId, timestamp: Seconds, nonce: Nonce, payloadHash: Option[PayloadHash],
  extendedData: Option[ExtendedData], mac: MAC)
