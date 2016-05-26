package finchtemplate.util.hawk.validate

import java.util.Base64
import javax.crypto
import javax.crypto.spec.SecretKeySpec

import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.{Base64Encoded, MACC}

trait MacOps {
  def mac(credentials: Credentials, data: Array[Byte]): MACC = MACC(base64Encode(createMac(credentials, data)))

  def base64Encode(data: Array[Byte]): Base64Encoded = Base64Encoded(Base64.getEncoder.encodeToString(data))

  private def createMac(credentials: Credentials, data: Array[Byte]): Array[Byte] = {
    val mac = crypto.Mac.getInstance(credentials.algorithm.keyGeneratorAlgorithm)
    val key = new SecretKeySpec(credentials.key.getBytes, credentials.algorithm.keyGeneratorAlgorithm)
    mac.init(key)
    mac.doFinal(data)
  }
}

object MacOps extends MacOps
