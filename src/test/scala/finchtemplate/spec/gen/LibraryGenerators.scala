package finchtemplate.spec.gen

import finchtemplate.util.time._
import org.joda.time.DateTime
import org.scalacheck.{Arbitrary, Gen}

trait LibraryGenerators {
  private lazy val now = DateTime.now()
  val genMillis = Gen.chooseNum(0L, now.plusYears(100).getMillis, now.getMillis).map(TaggedTypesFunctions.Millis)
  val genDateTime = genMillis.map(l => new DateTime(l))

  implicit def arbMillis: Arbitrary[Millis] = Arbitrary(genMillis)

  implicit def arbDateTime: Arbitrary[DateTime] = Arbitrary(genDateTime)
}
