package finchtemplate.spec.gen

import finchtemplate.api.v1.hello.Hello
import finchtemplate.util.time.TaggedTypesFunctions._
import finchtemplate.util.time.Time._
import finchtemplate.util.time.{Millis, Seconds, Time}
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}

trait DomainObjectGenerators {
  private lazy val now = nowUtc.asDateTime

  val genMillis: Gen[Millis] = Gen.chooseNum(0L, now.plusYears(100).getMillis, now.getMillis).map(Millis)
  val genSeconds: Gen[Seconds] = Gen.chooseNum(0L, time(now).asSeconds, time(now.plusYears(100)).asSeconds).map(Seconds)
  val genTime: Gen[Time] = Gen.chooseNum(0L, now.plusYears(100).getMillis, now.getMillis).map(m => time(Millis(m)))
  val genHello = alphaStr.map { n => Hello(n) }

  implicit def arbMillis: Arbitrary[Millis] = Arbitrary(genMillis)

  implicit def arbSeconds: Arbitrary[Seconds] = Arbitrary(genSeconds)

  implicit def arbTime: Arbitrary[Time] = Arbitrary(genTime)

  implicit def arbHello: Arbitrary[Hello] = Arbitrary(genHello)
}

object DomainObjectGenerators extends DomainObjectGenerators
