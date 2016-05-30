package finchtemplate.util.hawk.validate

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.HeaderValidationMethod
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params._
import finchtemplate.util.hawk.validate.TimeValidation.{acceptableTimeDelta, validate}
import finchtemplate.util.time.TaggedTypesFunctions.Millis
import finchtemplate.util.time.{Millis, TimeOps}
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class TimeValidationSpec extends Specification with SpecHelper {
  val credentials = Credentials(KeyId("fred"), Key("d0p1h1n5"), Sha256)

  val timestamps = new Properties("Timestamps") {
    property("invalid timestamps") = forAll { (ts: Millis) =>
      val delta = timeDeltaWithNow(ts)
      (delta > acceptableTimeDelta.getMillis) ==> (validate(credentials, context(ts), HeaderValidationMethod) must beXorLeft)
    }
    property("valid timestamps") = forAll { (ts: Millis) =>
      val delta = timeDeltaWithNow(ts)
      (delta >= 0L && delta <= acceptableTimeDelta.getMillis) ==> (validate(credentials, context(ts), HeaderValidationMethod) must beXorRight)
    }
  }

  s2"Validating timestamps$timestamps"

  private def timeDeltaWithNow(ts: Millis): Millis =
    Millis(math.abs(TimeOps.nowUtc.getMillis - TimeOps.utcTime(ts).getMillis))

  private def context(ts: Millis): RequestContext = {
    val header = RequestAuthorisationHeader(KeyId("fred"), ts, Nonce("nonce"), None, ExtendedData("data"), MAC(Base64Encoded("base64")))
    RequestContext(Get, Host("example.com"), Port(80), UriPath("/"), header, None)
  }
}
