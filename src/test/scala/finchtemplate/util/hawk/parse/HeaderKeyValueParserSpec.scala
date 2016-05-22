package finchtemplate.util.hawk.parse

import finchtemplate.spec.SpecHelper
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class HeaderKeyValueParserSpec extends Specification with ScalaCheck with SpecHelper {
  val invalidkeyValues = List(
    """="dh37fgj492je"""",
    """"dh37fgj492je"""",
    """dh37fgj492je""",
    """dh37fgj492je=""",
    """id="dh37fgj492je""""",
    """id=dh37fgj492je""""",
    """id="dh37fgj492je"""",
    """id=="dh37fgj492je"""",
    """id =="dh37fgj492je"""",
    """id== "dh37fgj492je"""",
    """id == "dh37fgj492je"""",
    """id==""dh37fgj492je"""",
    """id==""dh37fgj492je""""",
    """id=="dh37"fgj492je"""",
    """, ts="1353832234"""",
    """,, ts="1353832234"""",
    """ts="1353832234,"""",
    """ts="1353832234",""",
    """ts="1353832234",,""",
    """ts="1353832234""""",
    """ts="1353832234"""",
    """t=s="1353832234"""",
    """t$s="1353832234"""",
    """t@s="1353832234"""",
    """@t@s="1353832234"""",
    """@ts#="1353832234"""",
    """t1s="1353832234"""",
    """ts1="1353832234"""",
    """1ts="1353832234"""",
    """1ts1="1353832234""""
  )

  val genKnownInvalidHeaderValues:Gen[HeaderKeyValue]  = Gen.oneOf(invalidkeyValues)
  val genRandomStrings: Gen[HeaderKeyValue] = Arbitrary.arbString.arbitrary

  val invalidKeyValuesProp = new Properties("Invalid/unsupported header key/value parsing") {
    property("known invalid") = forAll(genKnownInvalidHeaderValues) { (header: HeaderKeyValue) =>
      val parsed = HeaderKeyValueParser.parse(header)
      parsed must beNone
    }
    property("generated invalid") = forAll(genRandomStrings) { (header: HeaderKeyValue) =>
      val parsed = HeaderKeyValueParser.parse(header)
      parsed must beNone
    }
  }

  s2"Parsing invalid/unsupported authentication headers$invalidKeyValuesProp"

  val knownGoodHeaderKeyValues = List(
    """ id="dh37fgj492je"""",
    """ id ="dh37fgj492je"""",
    """ id= "dh37fgj492je"""",
    """ id = "dh37fgj492je"""",
    """ id="dh37fgj492je """",
    """id="dh37fgj492je """",
    """ ts="1353832234"""",
    """ nonce="j4h3g2"""",
    """   hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY="""",
    """ext="some-app-ext-data"""",
    """ mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw=""""
  )

  val genKnownGoodHeaderValues: Gen[HeaderKeyValue] = Gen.oneOf(knownGoodHeaderKeyValues)
  val genHeaderKey: Gen[HeaderKey] = Gen.alphaStr
  val genHeaderValue: Gen[HeaderValue] = Gen.frequency((1, Gen.alphaStr), (1, Gen.numStr), (4, genKnownGoodHeaderValues))

  val parseProp = new Properties("Auth header key/value parsing") {
    property("known good key/values") = forAll(genKnownGoodHeaderValues) { (kv: HeaderKeyValue) =>
      val parsed = HeaderKeyValueParser.parse(kv)
      parsed must beSome
    }
    property("generated good key/values") = forAll(genHeaderKey, genHeaderValue) { (key: HeaderKey, value: HeaderValue) =>
      val parsed = HeaderKeyValueParser.parse(headerKeyValue(key, value))
      parsed must beSome(Map(key -> value))
    }
  }

  s2"Parsing valid authentication headers$parseProp"

  private def headerKeyValue(key: String, value: String): HeaderKeyValue = s""" $key="$value""""
}
