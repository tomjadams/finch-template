package finchtemplate.util.hawk

import finchtemplate.util.hawk.params.{KeyData, RequestContext}
import finchtemplate.util.hawk.validate.HeaderValidator.validateHeader
import finchtemplate.util.hawk.validate.PayloadValidator.validatePayload

/**
  * Authenticate an incoming request using Hawk.
  *
  * Performs header validation [1] by default, but will also do payload validation [2] if a `{{{Some(payload)}}} is
  * provided in the {{{RequestContext}}}.
  *
  * [1] https://github.com/hueniverse/hawk#protocol-example
  * [2] https://github.com/hueniverse/hawk#payload-validation
  **/
object HawkAuthenticate {
  def authenticate(key: KeyData, context: RequestContext): Boolean = {
    val headerValid = validateHeader(key, context.header)
    val payloadValid = context.payload.forall(validatePayload(key, _))
    headerValid && payloadValid
  }
}
