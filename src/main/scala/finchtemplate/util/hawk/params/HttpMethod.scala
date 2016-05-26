package finchtemplate.util.hawk.params

sealed trait HttpMethod {
  def headerCanonicalForm: String
}

case object Options extends HttpMethod {
  override val headerCanonicalForm = "OPTIONS"
}

case object Head extends HttpMethod {
  override val headerCanonicalForm = "HEAD"
}

case object Get extends HttpMethod {
  override val headerCanonicalForm = "GET"
}

case object Post extends HttpMethod {
  override val headerCanonicalForm = "HEAD"
}

case object Put extends HttpMethod {
  override val headerCanonicalForm = "HEAD"
}

case object Delete extends HttpMethod {
  override val headerCanonicalForm = "DELETE"
}

case object Trace extends HttpMethod {
  override val headerCanonicalForm = "TRACE"
}

case object Patch extends HttpMethod {
  override val headerCanonicalForm = "PATCH"
}
