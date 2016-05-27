package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk._
import finchtemplate.util.time.Millis

// TODO TJA Payload hash should be optional, it's only required for payload verification
final case class AuthorisationHeader(keyId: KeyId, timestamp: Millis, nonce: Nonce, payloadHash: PayloadHash,
  extendedData: ExtendedData, mac: MAC)
