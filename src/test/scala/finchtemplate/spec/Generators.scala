package finchtemplate.spec

import finchtemplate.api.v1.hello.Hello
import org.joda.time.DateTime
import org.scalacheck.Gen.{alphaStr, oneOf}
import org.scalacheck.{Arbitrary, Gen}

trait StdLibGenerators {
  val genExceptionNoCause = alphaStr.map { m => new java.lang.Exception(m) }
  val genExceptionCause: Gen[Exception] = for {
    m <- alphaStr
    c <- genExceptionNoCause
  } yield new java.lang.Exception(m, c)

  val genException = oneOf(genExceptionNoCause, genExceptionCause)

  implicit def arbException: Arbitrary[java.lang.Exception] = Arbitrary(genException)
}

trait LibraryGenerators {
  private lazy val now = DateTime.now()
  val genMillis = Gen.chooseNum(0, now.plusYears(100).getMillis, now.getMillis)
  val genDateTime = genMillis.map(l => new DateTime(l))

  implicit def arbDateTime: Arbitrary[DateTime] = Arbitrary(genDateTime)
}

trait DomainObjectGenerators {
  val genHello = alphaStr.map { n => Hello(n) }

  implicit def arbHello: Arbitrary[Hello] = Arbitrary(genHello)
}

trait Generators extends StdLibGenerators with LibraryGenerators with DomainObjectGenerators
