package finchtemplate.util.hawk.parse

import finchtemplate.spec.gen.Generators
import finchtemplate.util.hawk.{ExtendedDataTag, PayloadHashTag}
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.validate.MAC
import org.scalacheck.{Arbitrary, Gen}
import shapeless.tag.@@

object Arbitraries {
  implicit val arbKeyId = Arbitrary(Generators.genHexOfLength(12)(KeyId))
  implicit val arbNonce = Arbitrary(Gen.numStr.map(Nonce))
  implicit val arbPayloadHash = Arbitrary(Generators.genHexOfLength(44)(PayloadHash))
  implicit val arbOptionPayloadHash: Arbitrary[Option[String @@ PayloadHashTag]] = Arbitrary(Generators.genHexOfLength(44)(s => Some(PayloadHash(s))))
  implicit val arbExtendedData = Arbitrary(Gen.alphaStr.map(ExtendedData))
  implicit val arbOptionExtendedData: Arbitrary[Option[@@[String, ExtendedDataTag]]] = Arbitrary(Gen.alphaStr.map(s => Some(ExtendedData(s))))
  implicit val arbMac = Arbitrary(Generators.genHexOfLength(44)(h => MAC(Base64Encoded(h))))
}
