package finchtemplate.util.error

import finchtemplate.spec.NonDatabaseTestHelper
import org.specs2.mutable.Specification

final class ErrorOpsSpec extends Specification with NonDatabaseTestHelper {
  "Encode failure response" >> {
    "encodes failures into an error object" >> {
      val encoder = ErrorResponseEncoders.exceptionResponseEncoder
      val buff = encoder(new Exception("meh", new Exception("bzzt")))
      val output = new Array[Byte](buff.length)
      buff.write(output, 0)
      val encoded = new String(output)
      encoded must beEqualTo( """{"error":{"message":"meh","type":"Exception","cause":"bzzt"}}""")
    }
  }

  "JSON encoding" >> {
    "Throwable encode" >> {
      "without a cause" >> {
        val encoded = ErrorResponseEncoders.exceptionEncoder(new Exception("meh")).noSpaces
        encoded must beEqualTo( """{"message":"meh","type":"Exception","cause":"bzzt"}""")
      }
      "with cause" >> {
        val encoded = ErrorResponseEncoders.exceptionEncoder(new Exception("meh", new Exception("bzzt"))).noSpaces
        encoded must beEqualTo( """{"message":"meh","type":"Exception","cause":"bzzt"}""")
      }
    }
  }
}
