package finchtemplate.spec.gen

import finchtemplate.api.v1.hello.Hello
import finchtemplate.util.time.TaggedTypesFunctions._
import finchtemplate.util.time.Time._
import finchtemplate.util.time.{Millis, Seconds, Time}
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}

trait DomainObjectGenerators {
  private lazy val now = nowUtc.asDateTime

  val genTime: Gen[Time] = Gen.chooseNum(now.getMillis, now.plusYears(100).getMillis).map(ms => Time(Millis(ms)))
  val genMillis: Gen[Millis] = genTime.map(_.millis)
  val genSeconds: Gen[Seconds] = genTime.map(_.asSeconds)
  val genHello = alphaStr.map(n => Hello(n))

  implicit def arbMillis: Arbitrary[Millis] = Arbitrary(genMillis)

  implicit def arbSeconds: Arbitrary[Seconds] = Arbitrary(genSeconds)

  implicit def arbTime: Arbitrary[Time] = Arbitrary(genTime)

  implicit def arbHello: Arbitrary[Hello] = Arbitrary(genHello)
}

object DomainObjectGenerators extends DomainObjectGenerators
