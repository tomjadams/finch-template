package finchtemplate.util.hawk.validate

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.HeaderValidationType
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params._
import finchtemplate.util.time.TaggedTypesFunctions.Millis
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class HasherSpec extends Specification with ScalaCheck with SpecHelper {
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
  val header = HeaderContext(method, host, port, path, authHeader)

  val normalisedRequestString =
    s"""
       |$HeaderValidationType
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
      val hash = Hasher.hash(KeyData(keyId, key, Sha256), header)
      hash must beEqualTo(Hash.computeAndBase64Encode(normalisedRequestString, Sha256))
    }

    "can be hashed as SHA-512" >> {
      val hash = Hasher.hash(KeyData(keyId, key, Sha512), header)
      hash must beEqualTo(Hash.computeAndBase64Encode(normalisedRequestString, Sha512))
    }
  }
}
