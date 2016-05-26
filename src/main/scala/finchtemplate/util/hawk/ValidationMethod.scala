package finchtemplate.util.hawk

sealed trait ValidationMethod {
  def identifier: String
}

case object HeaderValidationMethod extends ValidationMethod {
  override val identifier = "hawk.1.header"
}

case object PayloadValidationMethod extends ValidationMethod {
  override val identifier = "hawk.1.payload"
}
