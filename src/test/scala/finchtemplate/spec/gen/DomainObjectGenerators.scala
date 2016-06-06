package finchtemplate.spec.gen

import finchtemplate.api.v1.hello.Hello
import finchtemplate.util.time.TaggedTypesFunctions._
import finchtemplate.util.time.Time.millisInSecond
import finchtemplate.util.time.{Millis, Seconds, Millis => _, Seconds => _, _}
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}
import shapeless.tag.@@

trait DomainObjectGenerators {
  private lazy val now = Time.nowUtc.asDateTime

  val genMillis: Gen[Millis] = Gen.chooseNum(0L, now.plusYears(100).getMillis, now.getMillis).map(Millis)
  val genSeconds: Gen[Seconds] = Gen.chooseNum(0L, Time.time(now).asSeconds, Time.time(now.plusYears(100)).asSeconds).map(Seconds)
  val genTime: Gen[Time] = Gen.chooseNum(0L, now.plusYears(100).getMillis, now.getMillis).map(m => Time(Millis(m)))
  val genHello = alphaStr.map { n => Hello(n) }

  implicit def arbMillis: Arbitrary[Millis] = Arbitrary(genMillis)
  implicit def arbSeconds: Arbitrary[Seconds] = Arbitrary(genSeconds)
  implicit def arbTime: Arbitrary[Time] = Arbitrary(genTime)
  implicit def arbHello: Arbitrary[Hello] = Arbitrary(genHello)
}

object DomainObjectGenerators extends DomainObjectGenerators
