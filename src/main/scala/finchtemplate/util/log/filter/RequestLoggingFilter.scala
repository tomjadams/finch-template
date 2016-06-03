package finchtemplate.util.log.filter

// Stolen from com.twitter.finagle.http.filter.LoggingFilter & adapted to SLF4J.

import com.twitter.finagle.filter.{LogFormatter => FinagleLogFormatter}
import com.twitter.finagle.http.filter.{CommonLogFormatter => FinagleCommonLogFormatter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util._
import finchtemplate.util.log.Logger

/**
 * Request logging filter.
 *
 * Logs all requests according to formatter.
 * Note this may be used upstream of a ValidateRequestFilter, so the URL and
 * parameters may be invalid.
 */
abstract class RequestLoggingFilter[R <: Request](val log: Logger, val formatter: FinagleLogFormatter[R, Response])
  extends LoggingFilter[R, Response] {

  // Treat exceptions as empty 500 errors
  override protected def logException(duration: Duration, request: R, throwable: Throwable) {
    val response = Response(request.version, Status.InternalServerError)
    val line = formatter.format(request, response, duration)
    log.info(line)
  }
}

object RequestLoggingFilter extends RequestLoggingFilter[Request](new Logger("access"), new FinagleCommonLogFormatter)
