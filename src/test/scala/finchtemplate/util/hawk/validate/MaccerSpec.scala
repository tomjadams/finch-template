package finchtemplate.util.hawk.validate

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params._
import finchtemplate.util.hawk.utils.MacOps
import finchtemplate.util.hawk.HeaderValidationMethod
import finchtemplate.util.time.TaggedTypesFunctions.Millis
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class MaccerSpec extends Specification with ScalaCheck with SpecHelper {
  val keyId = KeyId("username")
  val key = Key("password")

  val method = Get
  val host = Host("www.google.com")
  val port = Port(80)
  val path = UriPath("/search?q=hawk")
  val millis = Millis(System.currentTimeMillis())

  val nonce = Nonce("nonce-abc123")
  val extendedData = ExtendedData("this is some extra data")
  val authHeader = new AuthorisationHeader(keyId, millis, nonce, PayloadHash("hash-abcde"), extendedData, MAC(Base64Encoded("mac-12afc")))
  val header = HeaderContext(method, host, port, path, authHeader)
  val credentials = Credentials(keyId, key, Sha256)

  val normalisedRequestString =
    s"""
       |${HeaderValidationMethod.identifier}
       |$millis
       |$nonce
       |${method.headerCanonicalForm}
       |${path.path}
       |${host.host}
       |${port.port}
       |
       |$extendedData
    """.stripMargin.trim

  "Payload validation" >> {
    "" >> {
      false must beEqualTo(false)
    }
  }

  "Header validation" >> {
    "can be hashed as SHA-256" >> {
      val hash = Maccer.requestHash(credentials, RequestContext(header, None))
      hash must beEqualTo(MacOps.mac(credentials, normalisedRequestString.getBytes))
    }

    "can be hashed as SHA-512" >> {
      val hash = Maccer.requestHash(credentials, RequestContext(header, None))
      hash must beEqualTo(MacOps.mac(credentials, normalisedRequestString.getBytes))
    }
  }
}
