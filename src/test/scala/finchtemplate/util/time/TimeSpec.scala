package finchtemplate.util.time

import finchtemplate.spec.SpecHelper
import finchtemplate.util.time.Time._
import org.joda.time.DateTime
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class TimeSpec extends Specification with SpecHelper {
  val milliConversionProps = new Properties("Milliseconds to String conversions") {
    property("roundtrip parsing using millis") = forAll { (millis: Millis) =>
      val roundtrip = parseIsoAsTime(Time(millis).toIso8601)
      roundtrip must beSome(time(DateTime.parse(Time(millis).toIso8601, iso8601Parser)))
    }
    property("roundtrip parsing using milli strings") = forAll { (millis: Millis) =>
      val time = parseMillisAsTimeUtc(millis.toString)
      time must beSome(Time.time(new DateTime(millis)))
    }
    property("roundtrip parsing using seconds strings") = forAll { (seconds: Seconds) =>
      val time = parseSecondsAsTimeUtc(seconds.toString)
      time must beSome(Time.time(new DateTime(seconds * millisInSecond)))
    }
  }

  s2"Milliseconds to String conversions$milliConversionProps"

  val dateTimeConversionProps = new Properties("DateTime to String conversions") {
    property("roundtrip parsing using datetime") = forAll { (d: DateTime) =>
      val roundtrip = parseIsoAsTime(time(d).toIso8601)
      roundtrip must beSome(time(DateTime.parse(time(d).toIso8601, iso8601Parser)))
    }
  }

  s2"DateTime to String conversions$dateTimeConversionProps"

  val constructorProps = new Properties("Constructors") {
    property("millis") = forAll(genMillis) { (millis: Millis) =>
      Time(millis).millis must beEqualTo(millis)
      Time(millis).asSeconds must beEqualTo(millisToSeconds(millis))
    }
    property("seconds") = forAll(genSeconds) { (seconds: Seconds) =>
      time(seconds).asSeconds must beEqualTo(seconds)
      time(seconds).millis must beEqualTo(secondsToMillis(seconds))
    }
    property("datetime") = forAll(genDateTime) { (dateTime: DateTime) =>
      time(dateTime).asDateTime must beEqualTo(dateTime)
      time(dateTime).millis must beEqualTo(dateTime.getMillis)
    }
  }

  s2"Time constructors$constructorProps"

  val minusProps = new Properties("Minus") {
    property("millis") = forAll { (a: Time, b: Time) =>
      a.minus(b).getMillis must beEqualTo(a.millis - b.millis)
    }
  }

  s2"Minus function$minusProps"
}
