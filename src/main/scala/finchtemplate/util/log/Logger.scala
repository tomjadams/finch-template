package finchtemplate.util.log

import finchtemplate.config.Config
import org.slf4j.LoggerFactory

trait Logging {
  lazy val log = new Logger(Config.coreLoggerName)
}

object Logger extends Logging

final class Logger(name: String) {
  lazy val log = LoggerFactory.getLogger(name)

  def infoS(s: String) {
    log.info(s)
  }

  def errorST(s: String, t: Throwable) {
    log.error(s, t)
  }

  def errorS(s: String) {
    log.error(s)
  }
}
