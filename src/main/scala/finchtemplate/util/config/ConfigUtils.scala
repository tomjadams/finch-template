package finchtemplate.util.config

trait ConfigUtils {
  def envVar(name: String): Option[String] = Option(System.getenv(name))

  def envVarOrFail(name: String): String = {
    envVar(name) match {
      case Some(e) => e
      case None => sys.error(s"Required environment variable '$name' not found")
    }
  }

  def propertyVar(name: String): Option[String] = Option(System.getProperty(name))

  def propertyVarOrFail(name: String): String = {
    propertyVar(name) match {
      case Some(e) => e
      case None => sys.error(s"Required system property '$name' not found")
    }
  }
}

object ConfigUtils extends ConfigUtils
