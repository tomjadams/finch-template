package finchtemplate.spec

import java.lang.{Class => JavaClass}
import java.util.{Map => JavaMap}

import finchtemplate.util.log.Logger._

import scala.collection.JavaConversions._

trait TestEnvironmentSetter {
  def setEnvironment() = setEnvVars(Map("ENV" -> "test"))

  // Dirty hack, taken from http://stackoverflow.com/a/19040660
  private def setEnvVars(newenv: JavaMap[String, String]): Unit = {
    try {
      val processEnvironmentClass = JavaClass.forName("java.lang.ProcessEnvironment")
      val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
      theEnvironmentField.setAccessible(true)

      val variableClass = JavaClass.forName("java.lang.ProcessEnvironment$Variable")
      val convertToVariable = variableClass.getMethod("valueOf", classOf[java.lang.String])
      convertToVariable.setAccessible(true)

      val valueClass = JavaClass.forName("java.lang.ProcessEnvironment$Value")
      val convertToValue = valueClass.getMethod("valueOf", classOf[java.lang.String])
      convertToValue.setAccessible(true)

      val sampleVariable = convertToVariable.invoke(null, "")
      val sampleValue = convertToValue.invoke(null, "")
      val env = theEnvironmentField.get(null).asInstanceOf[JavaMap[sampleVariable.type, sampleValue.type]]
      newenv.foreach {
        case (k, v) => {
          val variable = convertToVariable.invoke(null, k).asInstanceOf[sampleVariable.type]
          val value = convertToValue.invoke(null, v).asInstanceOf[sampleValue.type]
          env.put(variable, value)
        }
      }

      val theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment")
      theCaseInsensitiveEnvironmentField.setAccessible(true)
      val cienv = theCaseInsensitiveEnvironmentField.get(null).asInstanceOf[JavaMap[String, String]]
      cienv.putAll(newenv);
    } catch {
      case e: NoSuchFieldException =>
        try {
          val classes = classOf[java.util.Collections].getDeclaredClasses
          val env = System.getenv()
          classes foreach { cl =>
            if ("java.util.Collections$UnmodifiableMap" == cl.getName) {
              val field = cl.getDeclaredField("m")
              field.setAccessible(true)
              val map = field.get(env).asInstanceOf[JavaMap[String, String]]
              // map.clear() // Not sure why this was in the code. It means we need to set all required environment variables.
              map.putAll(newenv)
            }
          }
        } catch {
          case e: Throwable => log.errorST("Unable to set test environment variables", e)
        }
      case e: Throwable => log.errorST("Unable to set test environment variables", e)
    }
  }
}
