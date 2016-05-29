package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk._
import finchtemplate.util.time.Millis

final case class RequestAuthorisationHeader(keyId: KeyId, timestamp: Millis, nonce: Nonce, payloadHash: Option[PayloadHash],
  extendedData: ExtendedData, mac: MAC)
