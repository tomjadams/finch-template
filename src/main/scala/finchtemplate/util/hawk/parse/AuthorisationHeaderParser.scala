package finchtemplate.util.hawk.parse

import finchtemplate.util.hawk.{AuthorisationHeader, _}

//Authorization: Hawk id="dh37fgj492je", ts="1353832234", nonce="j4h3g2", hash="Yi9LfIIFRtBEPt74PVmbTF/xVAwPn7ub15ePICfgnuY=", ext="some-app-ext-data", mac="aSe1DERmZuRl3pI36/9BdZmnErTw3sNzOOAUlfeKjVw="
object AuthorisationHeaderParser {
  private val headerPrefix = "Hawk "

  def parse(header: RawAuthenticationHeader): Option[AuthorisationHeader] = {
    if (header.startsWith(headerPrefix)) {
      val map: Array[Array[String]] = header.split(",").map(_.split("="))

      map.foldLeft(Map[String, String]())((acc, kv) => acc ++ Map(kv(0) -> kv(1)))


      None
    } else {
      None
    }
  }

}
