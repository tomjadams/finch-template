package finchtemplate.spec.gen

import org.scalacheck.Gen._
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
