package finchtemplate.config

import com.twitter.finagle.stats.{LoadedStatsReceiver, StatsReceiver}
import finchtemplate.util.config.ConfigUtils

object Config extends ConfigUtils {
  lazy val statsReceiver: StatsReceiver = LoadedStatsReceiver

  val systemId: String = "finch-template"

  val coreLoggerName: String = systemId

  def environment: String = envVar("ENV").getOrElse("development")

  def listenAddress: String = s":${envVar("PORT").getOrElse("8080")}"
}
