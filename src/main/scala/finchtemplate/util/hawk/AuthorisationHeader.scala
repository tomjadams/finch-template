package finchtemplate.util.hawk

import finchtemplate.util.time.Millis

final case class AuthorisationHeader(keyId: KeyId, timestamp: Millis, nonce: Nonce, payloadHash: PayloadHash,
  extendedData: ExtendedData, mac: MAC)
