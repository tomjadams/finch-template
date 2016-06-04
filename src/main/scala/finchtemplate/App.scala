package finchtemplate

import com.twitter.finagle.param.Stats
import com.twitter.finagle.{Http, ListeningServer}
import com.twitter.server.util.{JvmStats, TwitterStats}
import com.twitter.util.Await
import finchtemplate.config.Config
import finchtemplate.config.Environment.env
import finchtemplate.util.async.AsyncOps
import finchtemplate.util.error.ErrorReporter._
import finchtemplate.util.log.Logger._

final class App {
  lazy val server: ListeningServer = Http.server
    .withLabel(Config.systemId)
    .configured(Stats(Config.statsReceiver))
    .serve(Config.listenAddress, FinchTemplateApi.apiService)

  def boot(): Unit = {
    log.info(s"Booting ${Config.systemId} in ${env.name} mode on ${server.boundAddress}")
    sys.addShutdownHook(shutdown())
    registerMetricsAndMonitoring()
    Await.ready(server)
  }

  private def registerMetricsAndMonitoring(): Unit = {
    errorReporter.registerForUnhandledExceptions()
    JvmStats.register(Config.statsReceiver)
    TwitterStats.register(Config.statsReceiver)
  }

  private def shutdown(): Unit = {
    log.info(s"${Config.systemId} is shutting down...")
    Await.ready(server.close())
  }
}

object App {
  def main(args: Array[String]): Unit = {
    new App().boot()
  }
}
