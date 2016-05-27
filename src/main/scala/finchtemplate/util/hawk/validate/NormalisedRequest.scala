package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._

import finchtemplate.util.hawk.params.{PayloadContext, RequestContext}
import finchtemplate.util.hawk.{PayloadValidationMethod, _}

object NormalisedRequest {
  def normalisedHeaderMac(credentials: Credentials, context: RequestContext, normalisedPayloadMac: Option[MAC]): MAC = {
    val normalised =
      s"""
         |${HeaderValidationMethod.identifier}
         |${context.clientAuthHeader.timestamp}
         |${context.clientAuthHeader.nonce}
         |${context.method.headerCanonicalForm}
         |${context.path.path}
         |${context.host.host}
         |${context.port.port}
         |${normalisedPayloadMac.map(h => h.encoded).getOrElse("")}
         |${context.clientAuthHeader.extendedData}
      """.stripMargin.trim + "\n"
    MacOps.mac(credentials, normalised.getBytes(UTF_8))
  }

  def normalisedPayloadMac(credentials: Credentials, payload: PayloadContext): MAC = {
    val normalised =
      s"""
         |${PayloadValidationMethod.identifier}
         |${payload.contentType.contentType.toLowerCase}
         |${new String(payload.data, UTF_8)}
      """.stripMargin.trim + "\n"
    MacOps.mac(credentials, normalised.getBytes(UTF_8))
  }

}
