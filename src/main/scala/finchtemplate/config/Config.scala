package finchtemplate.config

import com.twitter.finagle.stats.{LoadedStatsReceiver, StatsReceiver}
import finchtemplate.util.config.ConfigUtils
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.validate.{Credentials, Sha256}

trait MonitoringConfig extends ConfigUtils {
  val rollbarAccessKey = envVarOrFail("ROLLBAR_ACCESS_KEY")
}

trait SystemConfig extends ConfigUtils {
  lazy val statsReceiver: StatsReceiver = LoadedStatsReceiver

  val systemId = "finch-template"

  val coreLoggerName = systemId

  def environment = envVarOrFail("ENV")

  def listenAddress = s":${envVarOrFail("PORT")}"

  val apiAuthenticationCredentials = Credentials(KeyId("API Client"), Key("4ef04c842e178c502e8ae4fa7d14dc6f"), Sha256)

  val miscThreadPoolSize = 100
}

object Config extends SystemConfig with MonitoringConfig with ConfigUtils
