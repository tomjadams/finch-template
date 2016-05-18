package finchtemplate.util.config

/**
 * An "environment" is a set of environment variables & a name.
 */
trait Environment {
  val name: String
  val isDevelopment: Boolean
  val isTest: Boolean
  val isProduction: Boolean
}
