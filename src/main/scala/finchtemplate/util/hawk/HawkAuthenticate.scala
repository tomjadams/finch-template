package finchtemplate.util.hawk

import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.hawk.validate.{Credentials, Maccer}

sealed trait AuthenticationResult

case object Invalid extends AuthenticationResult

case object Valid extends AuthenticationResult

/**
  * Authenticate an incoming request using Hawk.
  **/
object HawkAuthenticate {

  def authenticated(credentials: Credentials, context: RequestContext, method: ValidationMethod = PayloadValidationMethod): AuthenticationResult = {
    val computedMac = Maccer.requestMac(credentials, context, method)
    // TODO TJA Fix this.
    //computedMac.encodedForm == context.header.clientAuthHeader.mac
    Invalid
  }
}
