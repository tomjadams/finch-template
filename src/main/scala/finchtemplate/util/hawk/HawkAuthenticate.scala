package finchtemplate.util.hawk

import cats.data.Xor
import cats.data.Xor._
import com.github.benhutchison.mouse.all._
import finchtemplate.util.hawk.params.{PayloadContext, RequestContext}
import finchtemplate.util.hawk.parse.RequestAuthorisationHeaderParser
import finchtemplate.util.hawk.validate.Maccer.requestMac
import finchtemplate.util.hawk.validate._

sealed trait ValidationMethod {
  def identifier: String
}

case object HeaderValidationMethod extends ValidationMethod {
  override val identifier = "hawk.1.header"
}

case object PayloadValidationMethod extends ValidationMethod {
  override val identifier = "hawk.1.payload"
}

trait RequestValid

object HawkAuthenticate {
  /**
    * Parse a Hawk `Authorization` request header.
    **/
  def parseRawRequestAuthHeader(header: RawAuthenticationHeader): Option[RequestAuthorisationHeader] =
    RequestAuthorisationHeaderParser.parseAuthHeader(header)

  /**
    * Authenticate an incoming request using Hawk.
    **/
  def authenticateRequest(credentials: Credentials, context: RequestContext, method: ValidationMethod = PayloadValidationMethod): Xor[Error, RequestValid] = {
    // TODO TJA Validate the timestamp first, before any other processing.
    MacValidation.validateMac(credentials, context, method)
  }

  /**
    * Authenticate an outging response into a form suitable for adding to a `Server-Authorization` header.
    */
  def authenticateResponse(credentials: Credentials, payload: Option[PayloadContext]): ServerAuthorisationHeader = ???

  private def validateMac(computedMac: MAC, context: RequestContext): Xor[Error, RequestValid] =
    (computedMac == context.clientAuthHeader.mac).xor(error("Request MAC does not match computed MAC"), new RequestValid {})

  private def errorXor(message: String): Xor[Error, RequestValid] = left(error(message))

  private def error(message: String): Error = new Error(message)
}
