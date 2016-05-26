package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._

import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.RequestContext

object Maccer extends MacOps {
  def requestMac(key: Credentials, context: RequestContext, method: ValidationMethod): MACC = {
    ???
  }

  def requestHash(credentials: Credentials, context: RequestContext, payloadMac: Option[MACC] = None): MACC = {
    val normalisedRequestString =
      s"""
         |${HeaderValidationMethod.identifier}
         |${context.header.clientAuthHeader.timestamp}
         |${context.header.clientAuthHeader.nonce}
         |${context.header.method.headerCanonicalForm}
         |${context.header.path.path}
         |${context.header.host.host}
         |${context.header.port.port}
         |${payloadMac.map(h => h.encoded).getOrElse("")}
         |${context.header.clientAuthHeader.extendedData}
    """.stripMargin.trim
    Maccer.mac(credentials, normalisedRequestString.getBytes(UTF_8))
  }
}
