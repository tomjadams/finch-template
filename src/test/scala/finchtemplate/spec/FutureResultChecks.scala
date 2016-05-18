package finchtemplate.spec

import com.twitter.util.{Await, Future}
import org.specs2.execute.Result
import org.specs2.mutable.Specification

trait FutureResultChecks {
  self: Specification =>

  def checkResult[A](result: Future[A]): Result = {
    // Note. The `get` here forces the evaluation of the future, which will throw an exception if there is one (and thus fail the spec).
    Await.result(result.liftToTry).get()
    success("ok")
  }
}
