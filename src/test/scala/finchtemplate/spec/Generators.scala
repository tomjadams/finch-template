package finchtemplate.spec

import finchtemplate.api.v1.hello.Hello
import org.scalacheck.Gen.{alphaStr, oneOf}
import org.scalacheck.{Arbitrary, Gen}

trait StdLibGenerators {
  val genExceptionNoCause = alphaStr.map { m => new java.lang.Exception(m) }
  val genExceptionCause: Gen[Exception] = for {
    m <- alphaStr
    c <- genExceptionNoCause
  } yield new java.lang.Exception(m, c)

  val genException = oneOf(genExceptionNoCause, genExceptionCause)

  implicit def abException: Arbitrary[java.lang.Exception] = Arbitrary(genException)
}

trait DomainObjectGenerators {
  val genHello = alphaStr.map { n => Hello(n) }

  implicit def abHello: Arbitrary[Hello] = Arbitrary(genHello)
}

trait Generators extends StdLibGenerators with DomainObjectGenerators
