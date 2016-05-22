package finchtemplate.util.hawk.parse

import finchtemplate.util.hawk.parse.HeaderKeyValueParser.parseKeyValue
import finchtemplate.util.hawk.{AuthorisationHeader, _}
import finchtemplate.util.time.TimeOps._

//Authorization: Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="
object AuthorisationHeaderParser {
  private val headerPrefix = "Hawk "
  private val fieldId = "id"
  private val fieldTimeStamp = "ts"
  private val fieldNonce = "nonce"
  private val fieldPayloadHash = "hash"
  private val fieldExtendedData = "ext"
  private val fieldMac = "mac"

  def parseAuthHeader(header: RawAuthenticationHeader): Option[AuthorisationHeader] =
    if (header.startsWith(headerPrefix)) {
      parseSupportedHeader(header)
    } else {
      None
    }

  private def parseSupportedHeader(header: RawAuthenticationHeader): Option[AuthorisationHeader] = {
    val kvs = parseKeyValues(header)
    for {
      id <- kvs.get(fieldId)
      timestamp <- kvs.get(fieldTimeStamp).flatMap(parseMillisAsTime).map(_.getMillis)
      nonce <- kvs.get(fieldNonce)
      payloadHash <- kvs.get(fieldPayloadHash)
      extendedData <- kvs.get(fieldExtendedData)
      mac <- kvs.get(fieldMac)
    } yield AuthorisationHeader(id, timestamp, nonce, payloadHash, extendedData, mac)
  }

  private def parseKeyValues(header: RawAuthenticationHeader): Map[HeaderKey, HeaderValue] = {
    val kvs = header.split(",").map(kv => parseKeyValue(kv))
    kvs.foldLeft(Map[HeaderKey, HeaderValue]())((acc, maybeKv) => acc ++ maybeKv.getOrElse(Map()))
  }
}
