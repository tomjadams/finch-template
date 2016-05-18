package finchtemplate.util.log

//import com.twitter.logging.{Logger => FinagleLogger}

import org.slf4j.LoggerFactory

trait Logging {
  lazy val log = new Logger("liege")
}

object Logger extends Logging

final class Logger(name: String) {
  //  lazy val log = FinagleLogger(name)
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
