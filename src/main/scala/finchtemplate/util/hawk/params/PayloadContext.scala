package finchtemplate.util.hawk.params

object ContentType {
  val UnknownContentType: ContentType = ContentType("application/octet-stream")
}

final case class ContentType(contentType: String)

final case class PayloadContext(contentType: ContentType, data: Array[Byte])
