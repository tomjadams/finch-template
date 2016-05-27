package finchtemplate.util.hawk.params

final case class ContentType(contentType: String)

final case class PayloadContext(contentType: ContentType, data: Array[Byte])
