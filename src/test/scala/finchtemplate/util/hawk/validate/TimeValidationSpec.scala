package finchtemplate.util.hawk.validate

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.HeaderValidationMethod
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params._
import finchtemplate.util.hawk.validate.TimeValidation.acceptableTimeDelta
import finchtemplate.util.time.{Millis, TimeOps}
import org.joda.time.Seconds._
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class TimeValidationSpec extends Specification with SpecHelper {
  val credentials = Credentials(KeyId("fred"), Key("d0p1h1n5"), Sha256)

  val timestamps = new Properties("Timestamps") {
    property("invalid timestamps") = forAll { (ts: Millis) =>

      val delta = timeDelta(ts)
      val valid = TimeValidation.validate(credentials, context(ts), HeaderValidationMethod)

      (delta >= 0L && delta <= acceptableTimeDelta.getStandardSeconds) ==> ("" must beEqualTo(""))

    }
  }

  s2"Validating timestamps$timestamps"

  private def timeDelta(ts: Millis): Int = {
    math.abs(secondsBetween(TimeOps.nowUtc, TimeOps.utcTime(ts)).getSeconds)
  }

  private def context(ts: Millis): RequestContext = {
    val header = RequestAuthorisationHeader(KeyId("fred"), ts, Nonce("nonce"), None, ExtendedData("data"), MAC(Base64Encoded("base64")))
    RequestContext(Get, Host("example.com"), Port(80), UriPath("/"), header, None)
  }
}
