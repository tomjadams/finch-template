package finchtemplate

import com.twitter.finagle.Http
import com.twitter.util.Await
import finchtemplate.config.Config
import finchtemplate.config.Environment.env
import finchtemplate.util.log.Logger._

object App {
  def main(args: Array[String]): Unit = {
    new App().boot()
  }
}

final class App {
  private lazy val server = Http.serve(Config.listenAddress, FinchTemplateApi.apiService)

  def boot(): Unit = {
    log.infoS(s"Booting in ${env.name} mode on ${server.boundAddress}")
    sys.addShutdownHook(shutdown())
    Await.ready(server)
  }

  private def shutdown(): Unit = {
    log.infoS("Shutting down...")
    Await.ready(server.close())
  }
}
