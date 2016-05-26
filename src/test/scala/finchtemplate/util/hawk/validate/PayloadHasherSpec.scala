package finchtemplate.util.hawk.validate

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params.{HeaderContext, Port, UriPath, _}
import finchtemplate.util.hawk.{ExtendedData => _, Key => _, KeyId => _, Nonce => _, PayloadHash => _, _}
import finchtemplate.util.time.TaggedTypesFunctions._
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class PayloadHasherSpec extends Specification with ScalaCheck with SpecHelper {
  val keyId = KeyId("username")
  val key = Key("password")

  val method = Get
  val host = Host("www.google.com")
  val port = Port(80)
  val path = UriPath("/search?q=hawk")
  val millis = Millis(System.currentTimeMillis())

  val nonce = Nonce("nonce-abc123")
  val extendedData = ExtendedData("this is some extra data")
  val authHeader = new AuthorisationHeader(keyId, millis, nonce, PayloadHash("hash-abcde"), extendedData, Mac("mac-12afc"))
  val payload = PayloadContext(ContentType("text/plain"))

  val normalisedRequestString =
    s"""
       |$PayloadValidationType
       |$millis
       |$nonce
       |${method.headerCanonicalForm}
       |${path.path}
       |${host.host}
       |${port.port}
       |
       |$extendedData
    """.stripMargin.trim

  "A request header" >> {
    "can be hashed as SHA-256" >> {
      val hash = PayloadHasher.hash(KeyData(keyId, key, Sha256), payload)
      hash must beEqualTo(Hash.computeAndBase64Encode(normalisedRequestString, Sha256))
    }

    "can be hashed as SHA-512" >> {
      val hash = Hasher.hash(KeyData(keyId, key, Sha512), payload)
      hash must beEqualTo(Hash.computeAndBase64Encode(normalisedRequestString, Sha512))
    }
  }
}
