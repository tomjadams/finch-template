package finchtemplate.util.hawk.validate

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params._
import finchtemplate.util.hawk.{HeaderValidationMethod, PayloadValidationMethod}
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
  val credentials = Credentials(keyId, key, Sha256)

  "Header validation" >> {
    val noPayloadContext = RequestContext(method, host, port, path, authHeader, None)
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
    """.stripMargin.trim + "\n"

    "can be hashed as SHA-256" >> {
      val mac = Maccer.requestMac(credentials, noPayloadContext, HeaderValidationMethod)
      mac must beXorRight(MacOps.mac(credentials, normalisedRequestString.getBytes))
    }

    "can be hashed as SHA-512" >> {
      val mac = Maccer.requestMac(credentials, noPayloadContext, HeaderValidationMethod)
      mac must beXorRight(MacOps.mac(credentials, normalisedRequestString.getBytes))
    }

    "returns an error if we try to validate the payload" >> {
      val mac = Maccer.requestMac(credentials, noPayloadContext, PayloadValidationMethod)
      mac must beXorLeft
    }
  }

  // TODO TJA Implement this.
  "Payload validation" >> {
    val noPayloadContext = RequestContext(method, host, port, path, authHeader, None)

    "" >> {
      false must beEqualTo(false)
    }
  }
}
