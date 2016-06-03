package finchtemplate

import com.twitter.finagle.Http
import com.twitter.finagle.param.Stats
import com.twitter.server.util.{JvmStats, TwitterStats}
import com.twitter.util.Await
import finchtemplate.config.Config
import finchtemplate.config.Environment.env
import finchtemplate.util.log.Logger._

final class App {
  lazy val server = Http.server
    .withLabel(Config.systemId)
    .configured(Stats(Config.statsReceiver))
    .serve(Config.listenAddress, FinchTemplateApi.apiService)

  def boot(): Unit = {
    log.info(s"Booting in ${env.name} mode on ${server.boundAddress}")
    registerStats()
    sys.addShutdownHook(shutdown())
    Await.ready(server)
  }

  private def registerStats(): Unit = {
    JvmStats.register(Config.statsReceiver)
    TwitterStats.register(Config.statsReceiver)
  }

  private def shutdown(): Unit = {
    log.info("Shutting down...")
    Await.ready(server.close())
  }
}

object App {
  def main(args: Array[String]): Unit = {
    new App().boot()
  }
}
