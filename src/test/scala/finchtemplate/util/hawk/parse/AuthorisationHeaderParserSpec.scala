package finchtemplate.util.hawk.parse

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk._
import finchtemplate.util.time._
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class AuthorisationHeaderParserSpec extends Specification with ScalaCheck with SpecHelper {
  val noHawkId = List(
    """Hawke id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawkid="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Mac id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Auth id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""
  )
  val missingFields = List(
    """Hawk ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je, nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je", ts="1353832234", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data""""
  )
  val malformedFields = List(
    """Hawk id=dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je, ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je" ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF"/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some""-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""
  )

  val genKnownInvalidHeaders: Gen[RawAuthenticationHeader] = Gen.oneOf(noHawkId ++ missingFields ++ malformedFields)
  val genRandomStrings: Gen[RawAuthenticationHeader] = Arbitrary.arbString.arbitrary

  val invalidHeadersProp = new Properties("Invalid/unsupported header parsing") {
    property("known invalid headers") = forAll(genKnownInvalidHeaders) { (header: RawAuthenticationHeader) =>
      val parsed = AuthorisationHeaderParser.parseAuthHeader(header)
      parsed must beNone
    }
    property("random invalid headers") = forAll(genRandomStrings) { (header: RawAuthenticationHeader) =>
      val parsed = AuthorisationHeaderParser.parseAuthHeader(header)
      parsed must beNone
    }
  }

  s2"Parsing invalid/unsupported authentication headers$invalidHeadersProp"

  val parseProp = new Properties("Auth header parsing") {
    property("parsing") = forAll { (keyId: KeyId, timestamp: Millis, nonce: Nonce, payloadHash: PayloadHash, extendedData: ExtendedData, mac: MAC) =>
      val parsed = AuthorisationHeaderParser.parseAuthHeader(header(keyId, timestamp, nonce, payloadHash, extendedData, mac))
      parsed must beSome(AuthorisationHeader(keyId, timestamp, nonce, payloadHash, extendedData, mac))
    }
  }

  s2"Parsing authentication header$parseProp"

  private def header(keyId: KeyId, timestamp: Millis, nonce: Nonce, payloadHash: PayloadHash, extendedData: ExtendedData, mac: MAC): String =
    s"""Hawk id="$keyId", ts="$timestamp", nonce="$nonce", hash="$payloadHash", ext="$extendedData", mac="$mac""""
}
