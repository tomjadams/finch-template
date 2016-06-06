package finchtemplate.util.time

import finchtemplate.spec.SpecHelper
import org.joda.time.DateTime
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class TimeSpec extends Specification with SpecHelper {
  val milliConversionProps = new Properties("Milliseconds to String conversions") {
    property("roundtrip parsing using millis") = forAll { (millis: Millis) =>
      val roundtrip = Time.parseIsoAsTime(Time(millis).toIso8601)
      roundtrip must beSome(DateTime.parse(Time(millis).toIso8601, Time.iso8601Parser))
    }
    property("roundtrip parsing using milli strings") = forAll { (millis: Millis) =>
      val time = Time.parseMillisAsTimeUtc(millis.toString)
      time must beSome(new DateTime(millis))
    }
  }

  s2"Milliseconds to String conversions$milliConversionProps"

  val dateTimeConversionProps = new Properties("DateTime to String conversions") {
    property("roundtrip parsing using datetime") = forAll { (d: DateTime) =>
      val roundtrip = Time.parseIsoAsTime(Time.time(d).toIso8601)
      roundtrip must beSome(DateTime.parse(Time.time(d).toIso8601, Time.iso8601Parser))
    }
  }

  s2"DateTime to String conversions$dateTimeConversionProps"
}
