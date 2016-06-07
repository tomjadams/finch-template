package finchtemplate.util.hawk.validate

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.HeaderValidationMethod
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.params._
import finchtemplate.util.hawk.validate.TimeValidation.{acceptableTimeDelta, validate}
import finchtemplate.util.time.TaggedTypesFunctions.Seconds
import finchtemplate.util.time.Time.{nowUtc, time}
import finchtemplate.util.time._
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class TimeValidationSpec extends Specification with SpecHelper {
  val credentials = Credentials(KeyId("fred"), Key("d0p1h1n5"), Sha256)

  val timestamps = new Properties("Timestamps") {
    property("are valid if within the interval") = forAll { (time: Time) =>
      val delta = nowUtc.minus(time).getStandardSeconds
      if (delta > acceptableTimeDelta.getStandardSeconds) {
        validate(credentials, context(time), HeaderValidationMethod) must beXorLeft
      } else {
        validate(credentials, context(time), HeaderValidationMethod) must beXorRight
      }
    }
  }

  s2"Validating timestamps$timestamps"

  private def context(time: Time): RequestContext = {
    val header = RequestAuthorisationHeader(KeyId("fred"), time, Nonce("nonce"), None, Some(ExtendedData("data")), MAC(Base64Encoded("base64")))
    RequestContext(Get, Host("example.com"), Port(80), UriPath("/"), header, None)
  }
}
