package finchtemplate.util.hawk.parse

import finchtemplate.util.hawk.TaggedTypesFunctions._
import finchtemplate.util.hawk.parse.HeaderKeyValueParser.parseKeyValue
import finchtemplate.util.hawk.{AuthorisationHeader, _}
import finchtemplate.util.time.TaggedTypesFunctions.Millis
import finchtemplate.util.time.TimeOps._

object AuthorisationHeaderParser {
  private val headerPrefix = "Hawk "

  def parseAuthHeader(header: RawAuthenticationHeader): Option[AuthorisationHeader] =
    if (header.startsWith(headerPrefix)) {
      parseSupportedHeader(header)
    } else {
      None
    }

  private def parseSupportedHeader(header: RawAuthenticationHeader): Option[AuthorisationHeader] = {
    val kvs = parseKeyValues(header)
    for {
      id <- kvs.get(HeaderKey("id"))
      timestamp <- kvs.get(HeaderKey("ts")).flatMap(parseMillisAsTime).map(_.getMillis)
      nonce <- kvs.get(HeaderKey("nonce"))
      payloadHash <- kvs.get(HeaderKey("hash"))
      extendedData <- kvs.get(HeaderKey("ext"))
      mac <- kvs.get(HeaderKey("mac"))
    } yield AuthorisationHeader(KeyId(id), Millis(timestamp), Nonce(nonce), PayloadHash(payloadHash), ExtendedData(extendedData), Mac(mac))
  }

  private def parseKeyValues(header: RawAuthenticationHeader): Map[HeaderKey, HeaderValue] = {
    val kvs = header.split(",").map(kv => parseKeyValue(HeaderKeyValue(kv)))
    kvs.foldLeft(Map[HeaderKey, HeaderValue]())((acc, maybeKv) => acc ++ maybeKv.getOrElse(Map()))
  }
}
