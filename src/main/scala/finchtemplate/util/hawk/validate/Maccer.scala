package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._

import cats.data.Xor
import cats.data.Xor._
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.{PayloadContext, RequestContext}

object Maccer {

  // TODO TJA If the payload is available at the time of authentication, the server uses the hash value provided by the
  // client to construct the normalized string and validates the MAC. If the MAC is valid, the server calculates the
  // payload hash and compares the value with the provided payload hash in the header. In many cases, checking the MAC
  // first is faster than calculating the payload hash.
  //
  // It is important to note that MAC validation does not mean the hash value provided by the client is valid, only
  // that the value included in the header was not modified. Without calculating the payload hash on the server and
  // comparing it to the value provided by the client, the payload may be modified by an attacker.

  def requestMac(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[Error, MAC] =
    method match {
      case HeaderValidationMethod => validateHeader(credentials, context)
      case PayloadValidationMethod =>
        validatePayload(credentials, context).getOrElse(left(new Error("No payload provided for payload validation")))
    }

  private def validateHeader(credentials: Credentials, context: RequestContext): Xor[Error, MAC] =
    right(normalisedHeaderMac(credentials, context, None))

  private def validatePayload(credentials: Credentials, context: RequestContext): Option[Xor[Error, MAC]] = {
    context.payload.map { payloadContext =>
      // TODO TJA Pull the optional hash out of the header, when it's optional
      val payloadHash: Option[PayloadHash] = Some(context.clientAuthHeader.payloadHash)

      payloadHash match {
        case Some(clientHash: PayloadHash) => {
          right(normalisedHeaderMac(credentials, context, Some(MAC(Base64Encoded(clientHash)))))
        }
        case None => {
          val computedMac = MacOps.mac(credentials, payloadContext.data)
          val computedPayloadMac = normalisedPayloadMac(credentials, payloadContext, PayloadHash(computedMac.encoded))
          right(normalisedHeaderMac(credentials, context, Some(computedPayloadMac)))
        }
      }
    }
  }

  private def normalisedHeaderMac(credentials: Credentials, context: RequestContext, payloadMac: Option[MAC]): MAC = {
    val normalised =
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
    MacOps.mac(credentials, normalised.getBytes(UTF_8))
  }

  private def normalisedPayloadMac(credentials: Credentials, payload: PayloadContext, payloadHash: PayloadHash): MAC = {
    val normalised =
      s"""
         |${PayloadValidationMethod.identifier}
         |${payload.contentType.contentType.toLowerCase}
         |$payloadHash
      """.stripMargin.trim + "\n"
    MacOps.mac(credentials, normalised.getBytes(UTF_8))
  }
}
