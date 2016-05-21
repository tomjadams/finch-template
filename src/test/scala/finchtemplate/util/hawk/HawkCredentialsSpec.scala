package finchtemplate.util.hawk

import finchtemplate.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class HawkCredentialsSpec extends Specification with ScalaCheck with SpecHelper {
  val credsProp = new Properties("Credentials") {
    property("parsing") = forAll { (id: String, key: String) =>
      HawkCredentials(id, key, Sha256)
    }
  }

  s2"Parsing invalid algorithms$credsProp"
}
