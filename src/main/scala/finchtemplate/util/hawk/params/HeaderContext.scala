package finchtemplate.util.hawk.params

import finchtemplate.util.hawk.validate.AuthorisationHeader

case class UriPath(path: String)

case class Host(host: String)

case class Port(port: Int)

case class HeaderContext(method: HttpMethod, host: Host, port: Port, path: UriPath, authHeader: AuthorisationHeader)
