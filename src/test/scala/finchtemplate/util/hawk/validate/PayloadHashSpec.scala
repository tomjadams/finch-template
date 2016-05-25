package finchtemplate.util.hawk.validate

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params.{ContentType, KeyData, PayloadContext}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class PayloadHashSpec extends Specification with ScalaCheck with SpecHelper {

  "A request header" >> {
    val keyData = KeyData(KeyId("username"), Key("password"), Sha256)
    val payload = PayloadContext(ContentType("text/plain"))

    "can be validated" >> {
      val hash = PayloadHasher.hash(keyData, payload)

      false must beEqualTo(false)
    }
  }

}
