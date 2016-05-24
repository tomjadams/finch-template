package finchtemplate.util.hawk

import java.net.URI

case class Method(method: String)
case class Uri(uri: URI)
case class RequestContext(method: Method)

object HawkAuthenticate {

  def authenticate(context: RequestContext): Boolean = false

}
