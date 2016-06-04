package finchtemplate.util.log.filter

import com.twitter.finagle.filter.{LogFormatter => FinagleLogFormatter}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util._
import finchtemplate.util.log.Logger

/**
  * A [[com.twitter.finagle.Filter]] that logs all requests according to a formatter.
  */
trait LoggingFilter[REQ, REP] extends SimpleFilter[REQ, REP] {
  val log: Logger
  val formatter: FinagleLogFormatter[REQ, REP]

  def apply(request: REQ, service: Service[REQ, REP]): Future[REP] = {
    val elapsed = Stopwatch.start()
    val future = service(request)
    future respond {
      case Return(reply) =>
        logSuccess(elapsed(), request, reply)
      case Throw(throwable) =>
        logException(elapsed(), request, throwable)
    }
    future
  }

  protected def logSuccess(replyTime: Duration, request: REQ, reply: REP) {
    val line = formatter.format(request, reply, replyTime)
    log.info(line)
  }

  protected def logException(replyTime: Duration, request: REQ, throwable: Throwable) {
    val line = formatter.formatException(request, throwable, replyTime)
    log.errorST(line, throwable)
  }
}
