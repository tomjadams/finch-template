package finchtemplate.util.hawk

import finchtemplate.util.hawk.params.{KeyData, RequestContext}

object HawkAuthenticate {
  def authenticate(key: KeyData, context: RequestContext): Boolean = false
}
