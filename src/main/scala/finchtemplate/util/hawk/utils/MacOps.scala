package finchtemplate.util.hawk.utils

import javax.crypto
import javax.crypto.spec.SecretKeySpec

import finchtemplate.util.hawk.MAC
import finchtemplate.util.hawk.utils.Base64Ops._
import finchtemplate.util.hawk.validate.Credentials

trait MacOps {
  def mac(credentials: Credentials, data: Array[Byte]): MAC = MAC(base64Encode(createMac(credentials, data)))

  private def createMac(credentials: Credentials, data: Array[Byte]): Array[Byte] = {
    val mac = crypto.Mac.getInstance(credentials.algorithm.keyGeneratorAlgorithm)
    val key = new SecretKeySpec(credentials.key.getBytes, credentials.algorithm.keyGeneratorAlgorithm)
    mac.init(key)
    mac.doFinal(data)
  }
}

object MacOps extends MacOps
