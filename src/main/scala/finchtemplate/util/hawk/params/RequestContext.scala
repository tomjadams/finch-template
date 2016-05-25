package finchtemplate.util.hawk.params

case class RequestContext(header: HeaderContext, payload: Option[PayloadContext])

