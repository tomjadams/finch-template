package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._

import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.hawk.utils.MacOps

object Maccer extends MacOps {
  def requestMac(key: Credentials, context: RequestContext, method: ValidationMethod): MAC = {
    ???
  }

  def requestHash(credentials: Credentials, context: RequestContext, payloadMac: Option[MAC] = None): MAC = {
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
