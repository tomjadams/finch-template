package finchtemplate.util.error

import com.rollbar.Rollbar
import finchtemplate.config.Config._
import finchtemplate.config.Environment
import finchtemplate.util.config.Environment

trait ErrorReporter {
  def registerForUnhandledExceptions(): Unit

  def info(t: Throwable): Unit

  def warning(t: Throwable): Unit

  def error(t: Throwable): Unit
}

final class RollbarErrorReporter(accessToken: String, environment: Environment) extends ErrorReporter {
  private lazy val rollbar = new Rollbar(accessToken, environment.name)

  override def registerForUnhandledExceptions() = rollbar.handleUncaughtErrors()

  // TODO TJA Do these in the background, it shouldn't be synchronous
  override def info(t: Throwable) = rollbar.info(t)

  override def warning(t: Throwable) = rollbar.warning(t)

  override def error(t: Throwable) = rollbar.error(t)
}

object ErrorReporter {
  val errorReporter: ErrorReporter = new RollbarErrorReporter(rollbarAccessKey, Environment.env)
}
