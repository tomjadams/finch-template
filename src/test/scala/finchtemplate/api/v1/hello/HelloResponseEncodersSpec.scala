package finchtemplate.api.v1.hello

import finchtemplate.api.v1.hello.HelloResponseEncoders._
import finchtemplate.spec.SpecHelper
import io.circe.syntax._
import org.scalacheck.Prop._
import org.scalacheck.Properties
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class HelloResponseEncodersSpec extends Specification with SpecHelper {

  val encodeProp = new Properties("Hello encoding") {
    property("encode") = forAll { (h: Hello) =>
      h.asJson.noSpaces must beEqualTo(s"""{"hello":{"name":"${h.name}"}}""")
    }
  }

  s2"Hello can be encoded into JSON$encodeProp"

  val responseProp = new Properties("Hello response encoding") {
    property("encode") = forAll { (h: Hello) =>
      toResponseString(h) must beEqualTo(s"""{"data":{"hello":{"name":"${h.name}"}}}""")
    }
  }

  s2"Hello can be encoded into a response JSON$responseProp"
}
