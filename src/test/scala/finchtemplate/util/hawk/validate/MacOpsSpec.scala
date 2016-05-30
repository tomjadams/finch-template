package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._

import finchtemplate.spec.SpecHelper
import finchtemplate.util.hawk.TaggedTypesFunctions._
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

// Note. Test data generated with http://hash.online-convert.com/sha256-generator
final class MacOpsSpec extends Specification with SpecHelper {
  "Arrays of bytes" >> {
    "can have a SHA-256 MAC created" >> {
      val creds = Credentials(KeyId("fred"), Key("d0lph1n54r3c001"), Sha256)
      MacOps.mac(creds, "".getBytes(UTF_8)) must beEqualTo(MAC(Base64Encoded("mhiOwqitx35nIxKVmvYU3w/JmOmjGExRa0Qd/Mm+nuc=")))
      MacOps.mac(creds, "the cat in the hat".getBytes(UTF_8)) must beEqualTo(MAC(Base64Encoded("hJpSQNabjqx/gn1E5JSIEaCgCVtTlWMcdp+62WbQumc=")))
      MacOps.mac(creds,
        ("There is no expectation that the client will adjust its system clock to match the server (in fact, this would" +
          " be a potential attack vector). Instead, the client only uses the server's time to calculate an offset used " +
          "only for communications with that particular server. The protocol rewards clients with synchronized clocks " +
          "by reducing the number of round trips required to authenticate the first request."
          ).getBytes(UTF_8)
      ) must beEqualTo(MAC(Base64Encoded("X5hiTayxPp0IJzosiYlSojq2iRwAVL33JLD0oI3LT9w=")))
    }

    "can have a SHA-512 MAC created" >> {
      val creds = Credentials(KeyId("fred"), Key("d0lph1n54r3c001"), Sha512)
      MacOps.mac(creds, "".getBytes(UTF_8)) must beEqualTo(MAC(Base64Encoded("d6TUoj/rcgNSF/Qheb3Y671dk8Ic8mH8KTWo0gkuJHrsP+fnUmrcynOh7yyRctpIrKstWha9GM7dDAwsuprwDA==")))
      MacOps.mac(creds, "the cat in the hat".getBytes(UTF_8)) must beEqualTo(MAC(Base64Encoded("ggctPQsPjrGKbeEovn3R20dsIeMSrQzHqab/h0s/nkoemDQ+KhioX7beGplHduwr4ShZMVf+VboatpWv3d4cVQ==")))
      MacOps.mac(creds,
        ("There is no expectation that the client will adjust its system clock to match the server (in fact, this would" +
          " be a potential attack vector). Instead, the client only uses the server's time to calculate an offset used " +
          "only for communications with that particular server. The protocol rewards clients with synchronized clocks " +
          "by reducing the number of round trips required to authenticate the first request."
          ).getBytes(UTF_8)
      ) must beEqualTo(MAC(Base64Encoded("+NhK2UjwEH8Zn+K7N5vucUfHmYzU6MyVNVOhc4l/9mRRKtNzBiyM3iXOAe9/1UXNcbV+HtiITIcdimmdCcsa6Q==")))
    }
  }
}
