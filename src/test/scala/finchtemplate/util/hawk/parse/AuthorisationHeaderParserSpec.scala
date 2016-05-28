package finchtemplate.util.hawk.parse

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions.{ExtendedData => _, Nonce => _, PayloadHash => _, RawAuthenticationHeader => _}
import finchtemplate.util.hawk.validate.{AuthorisationHeader, MAC}
import finchtemplate.util.hawk.{TaggedTypesFunctions => UTTF, _}
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
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data""""
  )
  val malformedFields = List(
    """Hawk id=dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je, ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """Hawk id="dh37fgj492je" ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""
  )

  val genKnownInvalidHeaders: Gen[RawAuthenticationHeader] =
    Gen.oneOf((noHawkId ++ missingFields ++ malformedFields).map(UTTF.RawAuthenticationHeader))
  val genRandomStrings: Gen[RawAuthenticationHeader] = Arbitrary.arbString.arbitrary.map(UTTF.RawAuthenticationHeader)

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

  import Arbitraries._

  val knownGoodHeaders = List(
    """Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""
  )
  val genKnownValidHeaders: Gen[RawAuthenticationHeader] = Gen.oneOf(knownGoodHeaders).map(UTTF.RawAuthenticationHeader)

  val parseProp = new Properties("Auth header parsing") {
    property("known valid headers") = forAll(genKnownValidHeaders) { (header: RawAuthenticationHeader) =>
      val parsed = AuthorisationHeaderParser.parseAuthHeader(header)
      parsed must beSome
    }

    property("generated headers") = forAll {
      (keyId: KeyId, timestamp: Millis, nonce: Nonce, payloadHash: Option[PayloadHash], extendedData: ExtendedData, mac: MAC) =>
        val parsed = AuthorisationHeaderParser.parseAuthHeader(header(keyId, timestamp, nonce, payloadHash, extendedData, mac))
        parsed must beSome(new AuthorisationHeader(keyId, timestamp, nonce, payloadHash, extendedData, mac))
    }
  }

  s2"Parsing authentication header$parseProp"

  private def header(keyId: KeyId, timestamp: Millis, nonce: Nonce, payloadHash: Option[PayloadHash], extendedData: ExtendedData, mac: MAC): RawAuthenticationHeader = {
    val s = payloadHash match {
      case Some(hash) => s"""Hawk id="$keyId", ts="$timestamp", nonce="$nonce", hash="$hash", ext="$extendedData", mac="${mac.encoded}""""
      case None => s"""Hawk id="$keyId", ts="$timestamp", nonce="$nonce", ext="$extendedData", mac="${mac.encoded}""""
    }
    UTTF.RawAuthenticationHeader(s)
  }
}
