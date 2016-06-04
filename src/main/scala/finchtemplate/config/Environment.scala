package finchtemplate.config

import finchtemplate.config.Config.rollbarAccessKey
import finchtemplate.util.config.{ConfigUtils, Environment => Env}
import finchtemplate.util.error.RollbarErrorReporter

object Environment extends ConfigUtils {
  def env: Env = Config.environment match {
    case "development" => new Development
    case "test" => new Test
    case "production" => new Production
    case e => sys.error(s"Unknown environment '$e'")
  }
}

final class Development extends Env {
  override val name = "development"
  override val isDevelopment = true
  override val isTest = false
  override val isProduction = false
}

final class Test extends Env {
  override val name = "test"
  override val isDevelopment = false
  override val isTest = true
  override val isProduction = false
}

final class Production extends Env {
  override val name = "production"
  override val isDevelopment = false
  override val isTest = false
  override val isProduction = true
}
