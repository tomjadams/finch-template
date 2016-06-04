package finchtemplate.util.async

import com.twitter.util.{Future, FuturePool}

import scala.concurrent.ExecutionContext

trait AsyncOps {
  lazy val globalAsyncExecutionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  // TODO TJA Use the context above to run the futures
  def expensiveOp[T](f: => T): Future[T] = FuturePool.unboundedPool(f)
}

object AsyncOps extends AsyncOps
