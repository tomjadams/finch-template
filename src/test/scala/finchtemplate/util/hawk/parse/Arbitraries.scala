package finchtemplate.util.hawk.parse

import finchtemplate.util.hawk.TaggedTypesFunctions._
import org.scalacheck.Arbitrary

object Arbitraries {
  implicit val arbKeyId = Arbitrary(Arbitrary.arbString.arbitrary.map(KeyId))
  implicit val arbNonce = Arbitrary(Arbitrary.arbString.arbitrary.map(Nonce))
  implicit val arbPayloadHash = Arbitrary(Arbitrary.arbString.arbitrary.map(PayloadHash))
  implicit val arbExtendedData = Arbitrary(Arbitrary.arbString.arbitrary.map(ExtendedData))
  implicit val arbMac = Arbitrary(Arbitrary.arbString.arbitrary.map(Mac))
}
