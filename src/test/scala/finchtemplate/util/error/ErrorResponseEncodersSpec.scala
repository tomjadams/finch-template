package finchtemplate.util.error

import finchtemplate.spec.SpecHelper
import finchtemplate.util.error.ErrorResponseEncoders._
import io.circe.syntax._
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class ErrorResponseEncodersSpec extends Specification with ScalaCheck with SpecHelper {

  val encodeProp = new Properties("Hello encoding") {
    property("without exception cause") = forAll(genExceptionNoCause) { (e: java.lang.Exception) =>
      e.asJson.noSpaces == s"""{"message":"${e.getMessage}","type":"Exception"}"""
    }
    property("with exception cause") = forAll(genExceptionCause) { (e: java.lang.Exception) =>
      e.asJson.noSpaces == s"""{"message":"${e.getMessage}","type":"Exception","cause":"${e.getCause.getMessage}"}"""
    }
  }

  s2"Exception can be encoded into JSON$encodeProp"

  val responseProp = new Properties("Exception response encoding") {
    property("encode") = forAll(genException) { (e: java.lang.Exception) =>
      toResponseString(e) == s"""{"error":${e.asJson.noSpaces}}"""
    }
  }

  s2"Exception can be encoded into a response JSON$responseProp"
}
