package finchtemplate.util.hawk.validate

import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.{HeaderContext, KeyData}

object Hasher {
  def hash(key: KeyData, header: HeaderContext, payloadHash: Option[Hash] = None): Hash = {
    val normalisedRequestString =
      s"""
         |$HeaderValidationType
         |${header.authHeader.timestamp}
         |${header.authHeader.nonce}
         |${header.method.headerCanonicalForm}
         |${header.path.path}
         |${header.host.host}
         |${header.port.port}
         |${payloadHash.map(h => h.encodedForm).getOrElse("")}
         |${header.authHeader.extendedData}
    """.stripMargin.trim
    Hash.computeAndBase64Encode(normalisedRequestString, key.algorithm)
  }
}
