package finchtemplate.util.hawk.validate

import cats.data.Xor
import cats.data.Xor._
import com.github.benhutchison.mouse.all._
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.{PayloadContext, RequestContext}
import finchtemplate.util.hawk.validate.NormalisedRequest._

object Maccer {
  def requestMac(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[Error, MAC] =
    method match {
      case HeaderValidationMethod => validateHeader(credentials, context)
      case PayloadValidationMethod => validatePayload(credentials, context)
    }

  private def validateHeader(credentials: Credentials, context: RequestContext): Xor[Error, MAC] =
    right(normalisedHeaderMac(credentials, context, None))

  private def validatePayload(credentials: Credentials, context: RequestContext): Xor[Error, MAC] = {
    context.payload.map { payload =>
      context.clientAuthHeader.payloadHash.flatMap { clientProvidedHash =>
        val macFromClientProvidedHash = normalisedHeaderMac(credentials, context, Some(MAC(Base64Encoded(clientProvidedHash))))
        (macFromClientProvidedHash != context.clientAuthHeader.mac).option(errorXor("MAC provided in request does not match the computed MAC (payload hash may be invalid)"))
      }.getOrElse(right(completePayloadMac(credentials, context, payload)))
    }.getOrElse(errorXor("No payload provided for payload validation"))
  }

  private def completePayloadMac(credentials: Credentials, context: RequestContext, payloadContext: PayloadContext): MAC = {
    val computedPayloadMac = normalisedPayloadMac(credentials, payloadContext)
    normalisedHeaderMac(credentials, context, Some(computedPayloadMac))
  }
}
