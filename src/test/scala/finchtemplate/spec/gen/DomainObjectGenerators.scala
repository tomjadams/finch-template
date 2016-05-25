package finchtemplate.spec.gen

import finchtemplate.api.v1.hello.Hello
import org.scalacheck.Arbitrary
import org.scalacheck.Gen._

trait DomainObjectGenerators {
  val genHello = alphaStr.map { n => Hello(n) }

  implicit def arbHello: Arbitrary[Hello] = Arbitrary(genHello)
}
