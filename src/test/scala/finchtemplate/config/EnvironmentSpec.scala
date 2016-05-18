package finchtemplate.config

import finchtemplate.spec.NonDatabaseTestHelper
import org.specs2.mutable.Specification

final class EnvironmentSpec extends Specification with NonDatabaseTestHelper {
  val development = new Development()
  val test = new Test()
  val production = new Production()

  "When running tests" >> {
    "the environment is 'test'" >> {
      Environment.env.name must beEqualTo("test")
      Environment.env.isTest must beTrue
    }
  }

  "Environment details" >> {
    "development" >> {
      development.name mustEqual "development"
      development.isDevelopment must beTrue
      development.isTest must beFalse
      development.isProduction must beFalse
    }

    "test" >> {
      test.name mustEqual "test"
      test.isDevelopment must beFalse
      test.isTest must beTrue
      test.isProduction must beFalse
    }

    "production" >> {
      production.name mustEqual "production"
      production.isDevelopment must beFalse
      production.isTest must beFalse
      production.isProduction must beTrue
    }
  }
}
