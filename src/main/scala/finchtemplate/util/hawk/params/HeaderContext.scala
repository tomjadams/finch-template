package finchtemplate.util.hawk.params

import java.net.URI

import finchtemplate.util.hawk.validate.AuthorisationHeader
import finchtemplate.util.time._

case class UriPath(uri: URI)

case class Host(host: String)

case class Port(port: Int)

case class Time(millisSinceEpoch: Millis)

case class HeaderContext(method: HttpMethod, host: Host, uriPath: UriPath, time: Time, authHeader: AuthorisationHeader)
