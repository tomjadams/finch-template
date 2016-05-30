package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params._
import finchtemplate.util.hawk.validate.NormalisedRequest._
import finchtemplate.util.hawk.{HeaderValidationMethod, PayloadValidationMethod}
import finchtemplate.util.time.TaggedTypesFunctions.Millis
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class NormalisedRequestSpec extends Specification with SpecHelper {
  val keyId = KeyId("dh37fgj492je")
  val key = Key("werxhqb98rpaxn39848xrunpaw3489ruxnpa98w4rxn")

  val host = Host("example.com")
  val port = Port(8000)
  val path = UriPath("/resource/1?b=1&a=2")
  val millis = Millis(1353832234L)

  val nonce = Nonce("j4h3g2")
  val extendedData = ExtendedData("some-app-ext-data")
  val payloadHash = PayloadHash("Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=")
  val authHeader = new RequestAuthorisationHeader(keyId, millis, nonce, Some(payloadHash),
    extendedData, MAC(Base64Encoded("6R4rV5iE+NPoym+WwjeHzjAGXUtLNIxmo1vpMofpLAE=")))

  "Normalised headers without payload" >> {
    val method = Get
    val noPayloadRequestContext = RequestContext(method, host, port, path, authHeader, None)
    val normalisedRequest =
      s"""
         |${HeaderValidationMethod.identifier}
         |$millis
         |$nonce
         |${method.httpRequestLineMethod}
         |${path.path}
         |${host.host}
         |${port.port}
         |
         |$extendedData
      """.stripMargin.trim + "\n"

    "can be hashed as SHA-256" >> {
      val credentials = Credentials(keyId, key, Sha256)
      val mac = normalisedHeaderMac(credentials, noPayloadRequestContext, None)
      mac must beEqualTo(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }

    "can be hashed as SHA-512" >> {
      val credentials = Credentials(keyId, key, Sha512)
      val mac = normalisedHeaderMac(credentials, noPayloadRequestContext, None)
      mac must beEqualTo(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }
  }

  "Normalised headers with payload" >> {
    val method = Get
    val payloadContext = PayloadContext(ContentType("text/plain"), "Thank you for flying Hawk".getBytes(UTF_8))
    val withPayloadRequestContext = RequestContext(method, host, port, path, authHeader, Some(payloadContext))
    val normalisedRequest =
      s"""
         |${HeaderValidationMethod.identifier}
         |$millis
         |$nonce
         |${method.httpRequestLineMethod}
         |${path.path}
         |${host.host}
         |${port.port}
         |$payloadHash
         |$extendedData
      """.stripMargin.trim + "\n"

    "can be hashed as SHA-256" >> {
      val credentials = Credentials(keyId, key, Sha256)
      val mac = normalisedHeaderMac(credentials, withPayloadRequestContext, Some(MAC(Base64Encoded(payloadHash))))
      mac must beEqualTo(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }

    "can be hashed as SHA-512" >> {
      val credentials = Credentials(keyId, key, Sha512)
      val mac = normalisedHeaderMac(credentials, withPayloadRequestContext, Some(MAC(Base64Encoded(payloadHash))))
      mac must beEqualTo(MacOps.mac(credentials, normalisedRequest.getBytes(UTF_8)))
    }
  }

  "Normalised payloads" >> {
    val payload = PayloadContext(ContentType("text/plain"), "Thank you for flying Hawk".getBytes(UTF_8))

    "can be hashed as SHA-256" >> {
      val credentials = Credentials(keyId, key, Sha256)
      val mac = normalisedPayloadMac(credentials, payload)
      mac must beEqualTo(MacOps.mac(credentials, normalisedPayloadRequest(credentials, payload).getBytes(UTF_8)))
    }

    "can be hashed as SHA-512" >> {
      val credentials = Credentials(keyId, key, Sha512)
      val mac = normalisedPayloadMac(credentials, payload)
      mac must beEqualTo(MacOps.mac(credentials, normalisedPayloadRequest(credentials, payload).getBytes(UTF_8)))
    }
  }

  def normalisedPayloadRequest(credentials: Credentials, payload: PayloadContext): String = {
    s"""
       |${PayloadValidationMethod.identifier}
       |${payload.contentType.contentType.toLowerCase}
       |${new String(payload.data, UTF_8)}
    """.stripMargin.trim + "\n"
  }
}
