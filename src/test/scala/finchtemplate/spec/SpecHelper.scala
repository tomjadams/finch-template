package finchtemplate.spec

import finchtemplate.config.Config
import finchtemplate.spec.gen.Generators
import finchtemplate.util.log.Logger
import org.specs2.ScalaCheck
import org.specs2.execute.Failure
import org.specs2.matcher.XorMatchers
import org.specs2.specification.BeforeAll

trait SpecHelper extends TestEnvironmentSetter with ScalaCheck with Generators with JsonCodecHelper with XorMatchers with BeforeAll {
  lazy val log = new Logger(s"${Config.coreLoggerName}-test")

  override def beforeAll() = setEnvironment()

  def fail(message: String, t: Throwable) = Failure(message, "", t.getStackTrace.toList)
}
