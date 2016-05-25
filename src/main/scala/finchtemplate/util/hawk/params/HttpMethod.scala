package finchtemplate.util.hawk.params

sealed trait HttpMethod {
  def asString: String
}

case object Options extends HttpMethod {
  override val asString = "OPTIONS"
}

case object Head extends HttpMethod {
  override val asString = "HEAD"
}

case object Get extends HttpMethod {
  override val asString = "GET"
}

case object Post extends HttpMethod {
  override val asString = "HEAD"
}

case object Put extends HttpMethod {
  override val asString = "HEAD"
}

case object Delete extends HttpMethod {
  override val asString = "DELETE"
}

case object Trace extends HttpMethod {
  override val asString = "TRACE"
}

case object Patch extends HttpMethod {
  override val asString = "PATCH"
}
