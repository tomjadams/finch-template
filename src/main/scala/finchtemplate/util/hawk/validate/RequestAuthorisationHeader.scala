package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk._
import finchtemplate.util.time.Time

final case class RequestAuthorisationHeader(keyId: KeyId, timestamp: Time, nonce: Nonce, payloadHash: Option[PayloadHash],
  extendedData: Option[ExtendedData], mac: MAC)
