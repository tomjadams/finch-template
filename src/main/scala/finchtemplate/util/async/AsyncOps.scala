package finchtemplate.util.async

import scala.concurrent.ExecutionContext

trait AsyncOps {
  lazy val globalAsyncExecutionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
}

object AsyncOps extends AsyncOps
