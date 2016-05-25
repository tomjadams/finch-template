package finchtemplate.util.hawk.params

import java.net.URI

import finchtemplate.util.hawk.AuthorisationHeader
import finchtemplate.util.time.Millis

case class UriPath(uri: URI)

case class Host(host: String)

case class Time(millisSinceEpocj: Millis)

// request payload, put vs. post vs. get

case class RequestContext(method: HttpMethod, host: Host, uriPath: UriPath, time: Time, authHeader: AuthorisationHeader)

