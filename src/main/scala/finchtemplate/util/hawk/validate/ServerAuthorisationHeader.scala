package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk._

final case class ServerAuthorisationHeader(mac: MAC, payloadHash: PayloadHash, extendedData: ExtendedData)
