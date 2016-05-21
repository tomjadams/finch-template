package finchtemplate.util.hawk

object Algorithm {
  def algorithm(name: String): Option[Algorithm] = name match {
    case Sha256.name => Some(Sha256)
    case Sha512.name => Some(Sha512)
    case _ => None
  }
}

sealed trait Algorithm {
  def name: String

  def javaAlgorithmClass: String
}

case object Sha256 extends Algorithm {
  override val name = "sha256"

  override val javaAlgorithmClass = "HmacSHA256"
}

case object Sha512 extends Algorithm {
  override val name = "sha512"

  override val javaAlgorithmClass = "HmacSHA512"
}
