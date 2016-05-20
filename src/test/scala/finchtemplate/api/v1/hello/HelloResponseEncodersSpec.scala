package finchtemplate.api.v1.hello

import finchtemplate.api.v1.hello.HelloResponseEncoders._
import finchtemplate.spec.{JsonCodecHelper, NonDatabaseTestHelper}
import io.circe.syntax._
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class HelloResponseEncodersSpec extends Specification with ScalaCheck with JsonCodecHelper with NonDatabaseTestHelper {

  val genHello = for {v <- Gen.alphaStr} yield Hello(v)

  implicit def abHello: Arbitrary[Hello] = Arbitrary(genHello)

  val encodeProp = new Properties("Hello encoding") {
    property("encode") = forAll { (h: Hello) =>
      h.asJson.noSpaces == s"""{"hello":{"name":"${h.name}"}}"""
    }
  }

  s2"Hello can be encoded into JSON$encodeProp"

  val responseProp = new Properties("Hello response encoding") {
    property("encode") = forAll { (h: Hello) =>
      log.infoS(s"h.name: '${toResponseString(h)}'")
      toResponseString(h) == s"""{"data":{"hello":{"name":""}}}"""
      "" == ""
    }
  }

  s2"Responses can be encoded into JSON$responseProp"
}
