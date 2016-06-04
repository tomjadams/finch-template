package finchtemplate.spec.gen

import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}

trait StdLibGenerators {
  val genExceptionNoCause: Gen[Throwable] = alphaStr.map { m => new Throwable(m) }
  val genExceptionCause: Gen[Throwable] = for {
    m <- alphaStr
    c <- genExceptionNoCause
  } yield new Throwable(m, c)

  val genException = oneOf(genExceptionNoCause, genExceptionCause)

  implicit def arbException: Arbitrary[Throwable] = Arbitrary(genException)
}
