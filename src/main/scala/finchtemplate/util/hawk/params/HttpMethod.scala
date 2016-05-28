package finchtemplate.util.hawk.params

sealed trait HttpMethod {
  def httpRequestLineMethod: String
}

case object Options extends HttpMethod {
  override val httpRequestLineMethod = "OPTIONS"
}

case object Connect extends HttpMethod {
  override val httpRequestLineMethod = "CONNECT"
}

case object Head extends HttpMethod {
  override val httpRequestLineMethod = "HEAD"
}

case object Get extends HttpMethod {
  override val httpRequestLineMethod = "GET"
}

case object Post extends HttpMethod {
  override val httpRequestLineMethod = "POST"
}

case object Put extends HttpMethod {
  override val httpRequestLineMethod = "PUT"
}

case object Delete extends HttpMethod {
  override val httpRequestLineMethod = "DELETE"
}

case object Trace extends HttpMethod {
  override val httpRequestLineMethod = "TRACE"
}

case object Patch extends HttpMethod {
  override val httpRequestLineMethod = "PATCH"
}
