package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk._
import finchtemplate.util.time.Millis

final case class AuthorisationHeader(keyId: KeyId, timestamp: Millis, nonce: Nonce, payloadHash: PayloadHash,
  extendedData: ExtendedData, mac: finchtemplate.util.hawk.MACC)
