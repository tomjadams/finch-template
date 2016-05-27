package finchtemplate.util.hawk.params

import finchtemplate.util.hawk.validate.AuthorisationHeader

final case class UriPath(path: String)

final case class Host(host: String)

final case class Port(port: Int)

final case class RequestContext(method: HttpMethod, host: Host, port: Port, path: UriPath, clientAuthHeader: AuthorisationHeader, payload: Option[PayloadContext])
