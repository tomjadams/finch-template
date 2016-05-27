package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._

import finchtemplate.spec.SpecHelper
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

final class Base64OpsSpec extends Specification with ScalaCheck with SpecHelper {
  "Arrays of bytes" >> {
    "can be encoded into base64" >> {
      Base64Ops.base64Encode("".getBytes(UTF_8)) must beEqualTo("")
      Base64Ops.base64Encode("This is a slightly longer sentence, with a little punctuation.".getBytes(UTF_8)) must beEqualTo("VGhpcyBpcyBhIHNsaWdodGx5IGxvbmdlciBzZW50ZW5jZSwgd2l0aCBhIGxpdHRsZSBwdW5jdHVhdGlvbi4=")
      Base64Ops.base64Encode(
        ("There is no expectation that the client will adjust its system clock to match the server (in fact, this " +
          "would be a potential attack vector). Instead, the client only uses the server's time to calculate an offset " +
          "used only for communications with that particular server. The protocol rewards clients with synchronized " +
          "clocks by reducing the number of round trips required to authenticate the first request.").getBytes(UTF_8)
      ) must beEqualTo(
        "VGhlcmUgaXMgbm8gZXhwZWN0YXRpb24gdGhhdCB0aGUgY2xpZW50IHdpbGwgYWRqdXN0IGl0cyBzeXN0ZW0gY2xvY2sgdG8gbWF0Y2ggdGhlIHNlcnZlciAoaW4gZmFjdCwgdGhpcyB3b3VsZCBiZSBhIHBvdGVudGlhbCBhdHRhY2sgdmVjdG9yKS4gSW5zdGVhZCwgdGhlIGNsaWVudCBvbmx5IHVzZXMgdGhlIHNlcnZlcidzIHRpbWUgdG8gY2FsY3VsYXRlIGFuIG9mZnNldCB1c2VkIG9ubHkgZm9yIGNvbW11bmljYXRpb25zIHdpdGggdGhhdCBwYXJ0aWN1bGFyIHNlcnZlci4gVGhlIHByb3RvY29sIHJld2FyZHMgY2xpZW50cyB3aXRoIHN5bmNocm9uaXplZCBjbG9ja3MgYnkgcmVkdWNpbmcgdGhlIG51bWJlciBvZiByb3VuZCB0cmlwcyByZXF1aXJlZCB0byBhdXRoZW50aWNhdGUgdGhlIGZpcnN0IHJlcXVlc3Qu"
      )

    }
  }
}
