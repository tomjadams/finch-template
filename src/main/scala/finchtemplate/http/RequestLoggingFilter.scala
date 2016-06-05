package finchtemplate.http

import com.twitter.finagle.filter.{LogFormatter => FinagleLogFormatter}
import com.twitter.finagle.http.Status.InternalServerError
import com.twitter.finagle.http.filter.{CommonLogFormatter => FinagleCommonLogFormatter}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util._
import finchtemplate.util.log.Logger

/**
  * Request logging filter.
  *
  * If you have filters in the chain that modify status codes, you should place this filter before them, otherwise
  * the status codes logged will not be correct (they will be the pre-modified ones).
  *
  * Stolen from com.twitter.finagle.http.filter.LoggingFilter & adapted to SLF4J.
  */
abstract class RequestLoggingFilter[REQ <: Request](val log: Logger, val formatter: FinagleLogFormatter[REQ, Response])
  extends SimpleFilter[REQ, Response] {

  def apply(request: REQ, service: Service[REQ, Response]): Future[Response] = {
    val elapsed = Stopwatch.start()
    val future = service(request)
    future.respond {
      case Return(reply) => logSuccess(elapsed(), request, reply)
      case Throw(throwable) => logException(elapsed(), request, throwable)
    }
    future
  }

  def logSuccess(replyTime: Duration, request: REQ, reply: Response) {
    val line = formatter.format(request, reply, replyTime)
    log.info(line)
  }

  def logException(duration: Duration, request: REQ, throwable: Throwable) {
    val response = Response(request.version, InternalServerError)
    val line = formatter.format(request, response, duration)
    log.info(line)
  }
}

object RequestLoggingFilter extends RequestLoggingFilter[Request](new Logger("access"), new FinagleCommonLogFormatter)
