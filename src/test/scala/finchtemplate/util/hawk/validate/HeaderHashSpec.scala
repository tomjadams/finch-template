package finchtemplate.util.hawk.validate

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.HawkVersionHeader
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params._
import finchtemplate.util.time.TaggedTypesFunctions.Millis
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class HeaderHashSpec extends Specification with ScalaCheck with SpecHelper {
  val keyId = KeyId("username")
  val key = Key("password")

  val method = Get
  val host = Host("www.google.com")
  val port = Port(80)
  val path = UriPath("/search?q=hawk")
  val millis = Millis(System.currentTimeMillis())

  val nonce = Nonce("abc")
  val extendedData = ExtendedData("data")
  val authHeader = new AuthorisationHeader(keyId, millis, nonce, PayloadHash("abcde"), extendedData, Mac("12afc"))
  val header = HeaderContext(method, host, port, path, authHeader)

  val headerCanonicalRequest =
    s"""
       |$HawkVersionHeader
       |$millis
       |$nonce
       |${method.headerCanonicalForm}
       |$path
       |$host
       |$port
       |
       |$extendedData
    """.stripMargin

  "A request header" >> {
    "can be hashed as SHA-256" >> {
      val hash = HeaderHasher.hash(KeyData(keyId, key, Sha256), header)
      hash must beEqualTo(Hash.computeHash(headerCanonicalRequest, Sha256))
    }

    "can be hashed as SHA-512" >> {
      val hash = HeaderHasher.hash(KeyData(keyId, key, Sha512), header)
      hash must beEqualTo(Hash.computeHash(headerCanonicalRequest, Sha512))
    }
  }
}
