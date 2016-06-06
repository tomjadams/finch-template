package finchtemplate.spec.gen

import finchtemplate.util.time.TaggedTypesFunctions.{Millis, Seconds}
import finchtemplate.util.time.TimeOps._
import finchtemplate.util.time._
import org.joda.time.DateTime
import org.scalacheck.{Arbitrary, Gen}

trait LibraryGenerators {
  private lazy val now = nowUtc
  val genMillis = Gen.chooseNum(0L, now.plusYears(100).getMillis, now.getMillis).map(Millis)
  val genSeconds = Gen.chooseNum(0, millisToSeconds(Millis(now.plusYears(100).getMillis)), millisToSeconds(Millis(now.getMillis))).map(Seconds)
  val genDateTime = genMillis.map(m => utcTime(m))

  implicit def arbMillis: Arbitrary[Millis] = Arbitrary(genMillis)

  implicit def arbSeconds: Arbitrary[Seconds] = Arbitrary(genSeconds)

  implicit def arbDateTime: Arbitrary[DateTime] = Arbitrary(genDateTime)
}
