package finchtemplate.util.hawk.validate

import java.nio.charset.StandardCharsets._
import java.security.MessageDigest
import java.util.Base64

import shapeless.tag
import shapeless.tag._

trait Base64EncodedTag

object HashTypes {
  type Base64Encoded = String @@ Base64EncodedTag

  def Base64Encoded(s: String) = tag[Base64EncodedTag](s)
}

object Hash {
  def computeHash(s: String, algorithm: Algorithm): Hash = {
    val md = MessageDigest.getInstance(algorithm.javaAlgorithmName)
    md.update(s.getBytes(UTF_8))
    val encoded = Base64.getEncoder.encodeToString(md.digest())
    Hash(HashTypes.Base64Encoded(encoded), algorithm)
  }

}

case class Hash(hash: HashTypes.Base64Encoded, algorithm: Algorithm)
