package finchtemplate.config

import com.twitter.finagle.stats.{LoadedStatsReceiver, StatsReceiver}
import finchtemplate.util.config.ConfigUtils
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.validate.{Credentials, Sha256}

trait MonitoringConfig {
  val rollbarAccessKey = "4a9a760b7aec46d8952e8b49bb01af36"
}

trait SystemConfig extends ConfigUtils {
  lazy val statsReceiver: StatsReceiver = LoadedStatsReceiver

  val systemId = "finch-template"

  val coreLoggerName = systemId

  def environment = envVar("ENV").getOrElse("development")

  def listenAddress = s":${envVar("PORT").getOrElse("8080")}"

  val apiAuthenticationCredentials = Credentials(KeyId("API Client"), Key("4ef04c842e178c502e8ae4fa7d14dc6f"), Sha256)
}

object Config extends SystemConfig with MonitoringConfig with ConfigUtils
