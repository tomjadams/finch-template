package finchtemplate.config

import finchtemplate.util.config.ConfigUtils

object Config extends ConfigUtils {
  def coreLoggerName: String = "finch-template"

  def environment: String = envVar("ENV").getOrElse("development")

  def listenAddress: String = s":${envVar("PORT").getOrElse("8080")}"
}
