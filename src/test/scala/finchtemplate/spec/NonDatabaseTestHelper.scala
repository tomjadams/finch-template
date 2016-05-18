package finchtemplate.spec

import finchtemplate.util.log.Logger
import org.specs2.execute.Failure
import org.specs2.specification.BeforeAll

trait NonDatabaseTestHelper extends TestEnvironmentSetter with DataStubGenerator with BeforeAll {
  lazy val log = new Logger("finch-template-test")

  override def beforeAll() = setEnvironment()

  def fail(message: String, t: Throwable) = Failure(message, "", t.getStackTrace.toList)
}
