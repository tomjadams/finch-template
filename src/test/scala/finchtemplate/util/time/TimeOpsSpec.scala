package finchtemplate.util.time

import finchtemplate.spec.SpecHelper
import org.joda.time.DateTime
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class TimeOpsSpec extends Specification with ScalaCheck with SpecHelper {
  val milliConversionProps = new Properties("Milliseconds to String conversions") {
    property("roundtrip parsing using millis") = forAll { (millis: Millis) =>
      val roundtrip = TimeOps.parseAsTime(TimeOps.toIso8601(millis))
      roundtrip must beSome(DateTime.parse(TimeOps.toIso8601(millis), TimeOps.iso8601Parser))
    }
  }

  s2"Milliseconds to String conversions$milliConversionProps"

  val dateTimeConversionProps = new Properties("DateTime to String conversions") {
    property("roundtrip parsing using datetime") = forAll { (d: DateTime) =>
      val roundtrip = TimeOps.parseAsTime(TimeOps.toIso8601(d))
      roundtrip must beSome(DateTime.parse(TimeOps.toIso8601(d), TimeOps.iso8601Parser))
    }
  }

  s2"DateTime to String conversions$dateTimeConversionProps"
}
