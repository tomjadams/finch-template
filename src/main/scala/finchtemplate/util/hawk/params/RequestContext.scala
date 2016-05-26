package finchtemplate.util.hawk.params

final case class RequestContext(header: HeaderContext, payload: Option[PayloadContext])
