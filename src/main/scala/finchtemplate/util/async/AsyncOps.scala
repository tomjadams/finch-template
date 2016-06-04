package finchtemplate.util.async

import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.TimeUnit._

import com.twitter.util.{Future, FuturePool}
import finchtemplate.config.Config
import finchtemplate.util.log.Logger.log

import scala.concurrent.ExecutionContext

trait AsyncOps {
  lazy val executorService = newFixedThreadPool(Config.miscThreadPoolSize)
  lazy val futurePool = FuturePool.interruptible(executorService)
  lazy val globalAsyncExecutionContext: ExecutionContext = scala.concurrent.ExecutionContext.fromExecutor(executorService)

  def runAsync[T](f: => T): Future[T] = futurePool.apply(f)

  def shutdownExecutorService(): Unit = {
    log.info("Shutting down executor service...")
    executorService.shutdown()
    try {
      executorService.awaitTermination(30L, SECONDS)
    } catch {
      case e: InterruptedException => {
        log.warn("Interrupted while waiting for graceful shutdown, forcibly shutting down...")
        executorService.shutdownNow()
      }
    }
  }
}

object AsyncOps extends AsyncOps
