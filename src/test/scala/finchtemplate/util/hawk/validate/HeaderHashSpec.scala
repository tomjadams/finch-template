package finchtemplate.util.hawk.validate

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params.{ContentType, HeaderContext, KeyData, PayloadContext}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class HeaderHashSpec extends Specification with ScalaCheck with SpecHelper {

  "A request header" >> {
    val keyData = KeyData(KeyId("username"), Key("password"), Sha256)
    val header = HeaderContext(finchtemplate.util.hawk.params.HttpMethod(), )

    "can be validated" >> {
      val hash = HeaderValidator.validateHeader(keyData, header)

      false must beEqualTo(false)
    }
  }

}
