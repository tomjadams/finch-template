package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._

import cats.data.Xor
import cats.data.Xor._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.{PayloadContext, RequestContext}

object Maccer extends MacOps {
  def requestMac(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[Error, MAC] =
    method match {
      case HeaderValidationMethod => right(normalisedRequestMac(credentials, context, None))
      case PayloadValidationMethod => context.payload.map { c =>
        right(normalisedRequestMac(credentials, context, Some(normalisedPayloadMac(credentials, c))))
      }.getOrElse(left(new Error("No payload provided for payload validation")))
    }

  private def normalisedPayloadMac(credentials: Credentials, payload: PayloadContext): MAC = {
    val normalisedRequestString =
      s"""
         |${PayloadValidationMethod.identifier}
         |${payload.contentType.contentType.toLowerCase}
         |${Maccer.mac(credentials, payload.data)}
    """.stripMargin.trim + "\n"
    Maccer.mac(credentials, normalisedRequestString.getBytes(UTF_8))
  }

  private def normalisedRequestMac(credentials: Credentials, context: RequestContext, payloadMac: Option[MAC]): MAC = {
    val normalisedRequestString =
      s"""
         |${HeaderValidationMethod.identifier}
         |${context.clientAuthHeader.timestamp}
         |${context.clientAuthHeader.nonce}
         |${context.method.headerCanonicalForm}
         |${context.path.path}
         |${context.host.host}
         |${context.port.port}
         |${payloadMac.map(h => h.encoded).getOrElse("")}
         |${context.clientAuthHeader.extendedData}
    """.stripMargin.trim + "\n"
    Maccer.mac(credentials, normalisedRequestString.getBytes(UTF_8))
  }
}
