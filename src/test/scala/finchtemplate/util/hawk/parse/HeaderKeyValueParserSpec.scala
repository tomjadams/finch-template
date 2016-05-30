package finchtemplate.util.hawk.parse

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions.{HeaderKey, HeaderKeyValue, HeaderValue}
import finchtemplate.util.hawk._
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class HeaderKeyValueParserSpec extends Specification with SpecHelper {
  val invalidkeyValues = List(
    """a=b""",
    """a=1""",
    """="dh37fgj492je"""",
    """"dh37fgj492je"""",
    """dh37fgj492je""",
    """dh37fgj492je=""",
    """@ts#="1353832234""""
  ).map(HeaderKeyValue)

  val genKnownInvalidHeaderValues: Gen[HeaderKeyValue] = Gen.oneOf(invalidkeyValues)
  val genRandomStrings: Gen[HeaderKeyValue] = Arbitrary.arbString.arbitrary.map(HeaderKeyValue)

  val invalidKeyValuesProp = new Properties("Invalid/unsupported header key/value parsing") {
    property("known invalid") = forAll(genKnownInvalidHeaderValues) { (header: HeaderKeyValue) =>
      val parsed = HeaderKeyValueParser.parseKeyValue(header)
      parsed must beNone
    }
    property("generated invalid") = forAll(genRandomStrings) { (header: HeaderKeyValue) =>
      val parsed = HeaderKeyValueParser.parseKeyValue(header)
      parsed must beNone
    }
  }

  s2"Parsing invalid/unsupported authentication headers$invalidKeyValuesProp"

  val knownGoodHeaderKeyValues = List(
    """a=""""",
    """a="b"""",
    """a="1"""",
    """foo="bar"""",
    """ id="dh37fgj492je"""",
    """ id ="dh37fgj492je"""",
    """ id= "dh37fgj492je"""",
    """ id = "dh37fgj492je"""",
    """ id="dh37fgj492je """",
    """id="dh37fgj492je""""",
    """id="dh37fgj492je """",
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
    """ts="1353832234",,""",
    """ts="1353832234",""",
    """id="dh37fgj492je"""",
    """id=="dh37fgj492je"""",
    """id =="dh37fgj492je"""",
    """id== "dh37fgj492je"""",
    """id == "dh37fgj492je"""",
    """id==""dh37fgj492je"""",
    """id==""dh37fgj492je""""",
    """id=="dh37"fgj492je"""",
    """id=dh37fgj492je""""",
    """, ts="1353832234"""",
    """,, ts="1353832234"""",
    """ts="1353832234,"""",
    """ts="1353832234",""",
    """ ts="1353832234"""",
    """t1s="1353832234"""",
    """ts1="1353832234"""",
    """ts_1="1353832234"""",
    """ts_a1="1353832234"""",
    """ts_a_1="1353832234"""",
    """1ts1="1353832234"""",
    """ts1="1353832234"""",
    """1ts="1353832234"""",
    """1ts1="1353832234"""",
    """ nonce="j4h3g2"""",
    """   hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY="""",
    """ext="some-app-ext-data"""",
    """ mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""",
    """t@s="1353832234"""",
    """t=s="1353832234"""",
    """t$s="1353832234"""",
    """@t@s="1353832234""""
  ).map(HeaderKeyValue)
  val knownGoodHeaderValues = List(
    """b""",
    """1""",
    """bar""",
    """dh37fgj492je""",
    """1353832234""",
    """dh37fgj492je""",
    """j4h3g2""",
    """Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=""",
    """some-app-ext-data""",
    """some app ext data""",
    """aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="""
  ).map(HeaderValue)

  val genKnownGoodHeaderValues: Gen[HeaderValue] = Gen.oneOf(knownGoodHeaderValues)
  val genHeaderKey: Gen[HeaderKey] = Gen.identifier.map(HeaderKey)
  val genKnownGoodHeaderKeyValues: Gen[HeaderKeyValue] = Gen.oneOf(knownGoodHeaderKeyValues)

  val genHeaderValue: Gen[HeaderValue] = Gen.frequency(
    (1, Gen.alphaStr.map(HeaderValue)),
    (1, Gen.numStr.map(HeaderValue)),
    (4, genKnownGoodHeaderValues)
  )

  val parseProp = new Properties("Auth header key/value parsing") {
    property("known good key/values") = forAll(genKnownGoodHeaderKeyValues) { (kv: HeaderKeyValue) =>
      val parsed = HeaderKeyValueParser.parseKeyValue(kv)
      parsed must beSome
    }
    property("generated good key/values") = forAll(genHeaderKey, genHeaderValue) { (key: HeaderKey, value: HeaderValue) =>
      val parsed = HeaderKeyValueParser.parseKeyValue(headerKeyValue(key, value))
      parsed must beSome(Map(key.toLowerCase -> value))
    }
  }

  s2"Parsing valid authentication headers$parseProp"

  private def headerKeyValue(key: HeaderKey, value: HeaderValue): HeaderKeyValue = HeaderKeyValue(s"""$key="$value"""")
}
