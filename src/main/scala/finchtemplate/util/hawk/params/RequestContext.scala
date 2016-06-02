package finchtemplate.util.hawk.params

import finchtemplate.util.hawk.validate.RequestAuthorisationHeader

final case class UriPath(path: String)

object Host {
  val UnknownHost = Host("")
}

final case class Host(host: String)

final case class Port(port: Int)

final case class RequestContext(method: HttpMethod, host: Host, port: Port, path: UriPath, clientAuthHeader: RequestAuthorisationHeader, payload: Option[PayloadContext])

final case class ResponseContext(clientAuthHeader: RequestAuthorisationHeader, payload: Option[PayloadContext])
