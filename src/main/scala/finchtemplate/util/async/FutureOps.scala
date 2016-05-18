package finchtemplate.util.async

import com.twitter.util.{Future => TwitterFuture, Promise => TwitterPromise}

import scala.concurrent.{ExecutionContext, Future => ScalaFuture}
import scala.util.{Failure, Success}

trait FutureOps {
  private implicit lazy val executor: ExecutionContext = AsyncOps.globalAsyncExecutionContext

  def scalaToTwitterFuture[A](f: ScalaFuture[A]): TwitterFuture[A] = {
    val p = new TwitterPromise[A]()
    f.onComplete {
      case Success(value) => p.setValue(value)
      case Failure(exception) => p.setException(exception)
    }
    p
  }
}

object FutureOps extends FutureOps
