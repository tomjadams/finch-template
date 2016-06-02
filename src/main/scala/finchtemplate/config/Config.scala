package finchtemplate.config

import com.twitter.finagle.stats.{LoadedStatsReceiver, StatsReceiver}
import finchtemplate.util.config.ConfigUtils
import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.validate.{Credentials, Sha256}

object Config extends ConfigUtils {
  lazy val statsReceiver: StatsReceiver = LoadedStatsReceiver

  val systemId: String = "finch-template"

  val coreLoggerName: String = systemId

  def environment: String = envVar("ENV").getOrElse("development")

  def listenAddress: String = s":${envVar("PORT").getOrElse("8080")}"

  val httpAuthCredentials = Credentials(KeyId("API Client"), Key("4ef04c842e178c502e8ae4fa7d14dc6f"), Sha256)
}
