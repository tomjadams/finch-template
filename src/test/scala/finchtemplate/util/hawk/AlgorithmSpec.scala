package finchtemplate.util.hawk

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.validate.{Algorithm, Sha256, Sha512}
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class AlgorithmSpec extends Specification with ScalaCheck with SpecHelper {

  "Hawk algorithms" >> {
    "SHA 256" >> {
      Sha256.name must beEqualTo("sha256")
      Sha256.keyGeneratorAlgorithm must beEqualTo("HmacSHA256")
    }
    "SHA 512" >> {
      Sha512.name must beEqualTo("sha512")
      Sha512.keyGeneratorAlgorithm must beEqualTo("HmacSHA512")
    }
  }

  val invalidAlgorithmProp = new Properties("Invalid algorithms") {
    property("parsing") = forAll { (a: String) => Algorithm.algorithm(a) must beNone }
  }

  s2"Parsing invalid algorithms$invalidAlgorithmProp"

  "Parsing from string" >> {
    "returns the correct algorithm" >> {
      Algorithm.algorithm(Sha256.name) must beSome(Sha256)
      Algorithm.algorithm(Sha512.name) must beSome(Sha512)
    }
  }

}
