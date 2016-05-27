package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params._
import finchtemplate.util.hawk.{HeaderValidationMethod, PayloadValidationMethod}
import finchtemplate.util.time.TaggedTypesFunctions.Millis
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class MaccerSpec extends Specification with ScalaCheck with SpecHelper {
  val keyId = KeyId("dh37fgj492je")
  val key = Key("werxhqb98rpaxn39848xrunpaw3489ruxnpa98w4rxn")

  val method = Get
  val host = Host("example.com")
  val port = Port(8000)
  val path = UriPath("/resource/1?b=1&a=2")
  val millis = Millis(1353832234L)

  val nonce = Nonce("j4h3g2")
  val extendedData = ExtendedData("some-app-ext-data")
  val authHeader = new AuthorisationHeader(keyId, millis, nonce, PayloadHash("Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY="),
    extendedData, MAC(Base64Encoded("6R4rV5iE+NPoym+WwjeHzjAGXUtLNIxmo1vpMofpLAE=")))

  "Header validation" >> {
    val noPayloadRequestContext = RequestContext(method, host, port, path, authHeader, None)
    val normalisedRequest =
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
      val credentials = Credentials(keyId, key, Sha256)
      val mac = Maccer.requestMac(credentials, noPayloadRequestContext, HeaderValidationMethod)
      mac must beXorRight(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }

    "can be hashed as SHA-512" >> {
      val credentials = Credentials(keyId, key, Sha512)
      val mac = Maccer.requestMac(credentials, noPayloadRequestContext, HeaderValidationMethod)
      mac must beXorRight(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }

    "returns an error if we try to validate the payload" >> {
      val credentials = Credentials(keyId, key, Sha256)
      val mac = Maccer.requestMac(credentials, noPayloadRequestContext, PayloadValidationMethod)
      mac must beXorLeft
    }
  }

  "Payload validation" >> {
    val payload: PayloadContext = PayloadContext(ContentType("text/plain"), "some content".getBytes(UTF_8))
    val payloadRequestContext = RequestContext(method, host, port, path, authHeader, Some(payload))

    "can be hashed as SHA-256" >> {
      val credentials = Credentials(keyId, key, Sha256)
      val mac = Maccer.requestMac(credentials, payloadRequestContext, PayloadValidationMethod)
      mac must beXorRight(MacOps.mac(credentials, normalisedRequest(credentials, payload).getBytes(UTF_8)))
    }

    "can be hashed as SHA-512" >> {
      val credentials = Credentials(keyId, key, Sha512)
      val mac = Maccer.requestMac(credentials, payloadRequestContext, PayloadValidationMethod)
      mac must beXorRight(MacOps.mac(credentials, normalisedRequest(credentials, payload).getBytes(UTF_8)))
    }
  }

  def normalisedRequest(credentials: Credentials, payload: PayloadContext): String = {
    val normalisedPayload =
      s"""
         |${PayloadValidationMethod.identifier}
         |${payload.contentType.contentType.toLowerCase}
         |${MacOps.mac(credentials, payload.data)}
        """.stripMargin.trim + "\n"

    s"""
       |${HeaderValidationMethod.identifier}
       |$millis
       |$nonce
       |${method.headerCanonicalForm}
       |${path.path}
       |${host.host}
       |${port.port}
       |${MacOps.mac(credentials, normalisedPayload.getBytes(UTF_8))}
       |$extendedData
      """.stripMargin.trim + "\n"
  }
}
