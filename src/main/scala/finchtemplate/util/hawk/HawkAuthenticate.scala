package finchtemplate.util.hawk

import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.hawk.validate.{Credentials, Maccer}

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
  def authenticateRequest(credentials: Credentials, context: RequestContext, method: ValidationMethod = PayloadValidationMethod): AuthenticationResult =
    Maccer.requestMac(credentials, context, method).fold(
      error => Invalid(error),
      computedMac => if (computedMac == context.clientAuthHeader.mac) Valid else Invalid(new Error("Request MAC does not match"))
    )
}
