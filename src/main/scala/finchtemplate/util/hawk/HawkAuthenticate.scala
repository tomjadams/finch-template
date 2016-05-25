package finchtemplate.util.hawk

import finchtemplate.util.hawk.params.{KeyData, RequestContext}
import finchtemplate.util.hawk.validate.HeaderValidator.validateHeader
import finchtemplate.util.hawk.validate.PayloadValidator.validatePayload

/*
 * Authenticate an incoming request using Hawk.
 */
object HawkAuthenticate {
  def authenticate(key: KeyData, context: RequestContext): Boolean = {
    val headerValid = validateHeader(key, context.header)
    val payloadValid = context.payload.forall(validatePayload(key, _))
    headerValid && payloadValid
  }
}
