package finchtemplate.util.hawk.parse

import finchtemplate.util.hawk.TaggedTypesFunctions._
import org.scalacheck.{Arbitrary, Gen}

object Arbitraries {
  implicit val arbKeyId = Arbitrary(Gen.alphaStr.map(KeyId))
  implicit val arbNonce = Arbitrary(Gen.alphaStr.map(Nonce))
  implicit val arbPayloadHash = Arbitrary(Gen.alphaStr.map(PayloadHash))
  implicit val arbExtendedData = Arbitrary(Gen.alphaStr.map(ExtendedData))
  implicit val arbMac = Arbitrary(Gen.alphaStr.map(Mac))
}
