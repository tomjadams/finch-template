package finchtemplate.util.hawk

import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.hawk.parse.AuthorisationHeaderParser
import finchtemplate.util.hawk.validate.{AuthorisationHeader, Credentials, MAC, Maccer}

sealed trait ValidationMethod {
  def identifier: String
}

case object HeaderValidationMethod extends ValidationMethod {
  override val identifier = "hawk.1.header"
}

case object PayloadValidationMethod extends ValidationMethod {
  override val identifier = "hawk.1.payload"
}

sealed trait AuthenticationResult

case class Invalid(error: Error) extends AuthenticationResult

case object Valid extends AuthenticationResult

/**
  * Authenticate an incoming request using Hawk.
  **/
object HawkAuthenticate {
  def parseRawRequestAuthHeader(header: RawAuthenticationHeader): Option[AuthorisationHeader] =
    AuthorisationHeaderParser.parseAuthHeader(header)

  def authenticateRequest(credentials: Credentials, context: RequestContext, method: ValidationMethod = PayloadValidationMethod): AuthenticationResult =
    Maccer.requestMac(credentials, context, method).fold(
      error => Invalid(error),
      computedMac => validateMac(computedMac, context)
    )

  private def validateMac(computedMac: MAC, context: RequestContext): AuthenticationResult = {
    if (computedMac == context.clientAuthHeader.mac) {
      // TODO TJA Validate the timestamp too.
      Valid
    } else {
      Invalid(new Error("Request MAC does not match computed MAC"))
    }
  }
}
