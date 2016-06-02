package finchtemplate.util.hawk.params

import finchtemplate.spec.SpecHelper
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class HttpMethodSpec extends Specification with SpecHelper {

  val invalidAlgorithmProp = new Properties("Invalid methods") {
    property("parsing") = forAll { (a: String) => HttpMethod.httpMethod(a) must beNone }
  }

  s2"Parsing invalid algorithms$invalidAlgorithmProp"

  "Parsing from string" >> {
    "returns the correct method (uppercase)" >> {
      HttpMethod.httpMethod(Options.httpRequestLineMethod) must beSome(Options)
      HttpMethod.httpMethod(Connect.httpRequestLineMethod) must beSome(Connect)
      HttpMethod.httpMethod(Head.httpRequestLineMethod) must beSome(Head)
      HttpMethod.httpMethod(Get.httpRequestLineMethod) must beSome(Get)
      HttpMethod.httpMethod(Post.httpRequestLineMethod) must beSome(Post)
      HttpMethod.httpMethod(Put.httpRequestLineMethod) must beSome(Put)
      HttpMethod.httpMethod(Delete.httpRequestLineMethod) must beSome(Delete)
      HttpMethod.httpMethod(Trace.httpRequestLineMethod) must beSome(Trace)
      HttpMethod.httpMethod(Patch.httpRequestLineMethod) must beSome(Patch)
    }
    "returns the correct method (lowercase)" >> {
      HttpMethod.httpMethod(Options.httpRequestLineMethod.toLowerCase) must beSome(Options)
      HttpMethod.httpMethod(Connect.httpRequestLineMethod.toLowerCase) must beSome(Connect)
      HttpMethod.httpMethod(Head.httpRequestLineMethod.toLowerCase) must beSome(Head)
      HttpMethod.httpMethod(Get.httpRequestLineMethod.toLowerCase) must beSome(Get)
      HttpMethod.httpMethod(Post.httpRequestLineMethod.toLowerCase) must beSome(Post)
      HttpMethod.httpMethod(Put.httpRequestLineMethod.toLowerCase) must beSome(Put)
      HttpMethod.httpMethod(Delete.httpRequestLineMethod.toLowerCase) must beSome(Delete)
      HttpMethod.httpMethod(Trace.httpRequestLineMethod.toLowerCase) must beSome(Trace)
      HttpMethod.httpMethod(Patch.httpRequestLineMethod.toLowerCase) must beSome(Patch)
    }
  }
}
