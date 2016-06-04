package finchtemplate.util.error

import finchtemplate.spec.SpecHelper
import finchtemplate.util.error.ErrorResponseEncoders._
import io.circe.syntax._
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.specs2.mutable.Specification

final class ErrorResponseEncodersSpec extends Specification with SpecHelper {

  val encodeProp = new Properties("Hello encoding") {
    property("without exception cause") = forAll(genExceptionNoCause) { (e: Throwable) =>
      e.asJson.noSpaces must beEqualTo(s"""{"message":"${e.getMessage}","type":"Throwable"}""")
    }
    property("with exception cause") = forAll(genExceptionCause) { (e: Throwable) =>
      e.asJson.noSpaces must beEqualTo(s"""{"message":"${e.getMessage}","type":"Throwable","cause":"${e.getCause.getMessage}"}""")
    }
  }

  s2"Exception can be encoded into JSON$encodeProp"

  val responseProp = new Properties("Exception response encoding") {
    property("encode") = forAll(genException) { (e: Throwable) =>
      jsonString(e) must beEqualTo(s"""{"error":${e.asJson.noSpaces}}""")
    }
  }

  s2"Exception can be encoded into a response JSON$responseProp"
}
