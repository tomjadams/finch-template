package finchtemplate.util.hawk.parse

import finchtemplate.util.hawk.parse.HeaderKeyValueParser.parseKeyValue
import finchtemplate.util.hawk.{AuthorisationHeader, _}

//Authorization: Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="
object AuthorisationHeaderParser {
  private val headerPrefix = "Hawk "
  private val fieldId = "id"
  private val fieldTimeStamp = "ts"
  private val fieldNonce = "nonce"
  private val fieldPayloadHash = "hash"
  private val fieldExtendedData = "ext"
  private val fieldMac = "mac"

  def parseAuthHeader(header: RawAuthenticationHeader): Option[AuthorisationHeader] = {
    if (header.startsWith(headerPrefix)) {
      val kvs: Map[HeaderKey, HeaderValue] = parseKeyValues(header)

      val id = kvs.get(fieldId)
      val timestamp = kvs.get(fieldTimeStamp)
      val nonce = kvs.get(fieldNonce)
      val payloadHash = kvs.get(fieldPayloadHash)
      val extendedData = kvs.get(fieldExtendedData)
      val mac: Option[HeaderValue] = kvs.get(fieldMac)


      kvs

      None
    } else {
      None
    }
  }

  private def parseKeyValues(header: RawAuthenticationHeader): Map[HeaderKey, HeaderValue] = {
    val kvs = header.split(",").map(kv => parseKeyValue(kv))
    kvs.foldLeft(Map[HeaderKey, HeaderValue]())((acc, maybeKv) => acc ++ maybeKv.getOrElse(Map()))
  }
}
