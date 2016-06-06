package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params._
import finchtemplate.util.hawk.validate.NormalisedRequest.{normalisedHeaderMac, normalisedPayloadMac}
import finchtemplate.util.hawk.{HeaderValidationMethod, PayloadValidationMethod}
import finchtemplate.util.time.TaggedTypesFunctions.Seconds
import finchtemplate.util.time.Time
import finchtemplate.util.time.Time.time
import org.specs2.mutable.Specification

final class MaccerSpec extends Specification with SpecHelper {
  val keyId = KeyId("dh37fgj492je")
  val key = Key("werxhqb98rpaxn39848xrunpaw3489ruxnpa98w4rxn")

  val host = Host("example.com")
  val port = Port(8000)
  val path = UriPath("/resource/1?b=1&a=2")
  val timestamp = time(Seconds(1465190788))

  val nonce = Nonce("j4h3g2")
  val extendedData = ExtendedData("some-app-ext-data")

  "Header validation" >> {
    val method = Get
    val authHeader = new RequestAuthorisationHeader(keyId, timestamp, nonce, Some(PayloadHash("Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=")),
      Some(extendedData), MAC(Base64Encoded("6R4rV5iE+NPoym+WwjeHzjAGXUtLNIxmo1vpMofpLAE=")))
    val noPayloadRequestContext = RequestContext(method, host, port, path, authHeader, None)
    val normalisedRequest =
      s"""
         |${HeaderValidationMethod.identifier}
         |$timestamp
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
    val payload = PayloadContext(ContentType("text/plain"), "Thank you for flying Hawk".getBytes(UTF_8))

    "can be hashed" >> {
      val authHeader = new RequestAuthorisationHeader(keyId, timestamp, nonce, Some(PayloadHash("Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=")),
        Some(extendedData), MAC(Base64Encoded("aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=")))
      val requestContext = RequestContext(Post, host, port, path, authHeader, Some(payload))
      val credentials = Credentials(keyId, key, Sha256)
      val mac = Maccer.requestMac(credentials, requestContext, PayloadValidationMethod)
      mac must beXorRight(normalisedRequestMac(credentials, requestContext, payload))
    }

    "failure is short circuited if an invalid hash is provided by the client" >> {
      val authHeader = new RequestAuthorisationHeader(keyId, timestamp, nonce, Some(PayloadHash("INVALID HASH")),
        Some(extendedData), MAC(Base64Encoded("6R4rV5iE+NPoym+WwjeHzjAGXUtLNIxmo1vpMofpLAE=")))
      val requestContext = RequestContext(Post, host, port, path, authHeader, Some(payload))
      val credentials = Credentials(keyId, key, Sha256)
      val mac = Maccer.requestMac(credentials, requestContext, PayloadValidationMethod)
      mac must beXorLeft
    }
  }

  def normalisedRequestMac(credentials: Credentials, context: RequestContext, payload: PayloadContext): MAC = {
    normalisedHeaderMac(credentials, context, Some(normalisedPayloadMac(credentials, payload)))
  }
}
