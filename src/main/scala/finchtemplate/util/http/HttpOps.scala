package finchtemplate.util.http

import java.nio.charset.StandardCharsets._
import java.util.concurrent.TimeUnit

import com.twitter.finagle.http.Cookie
import com.twitter.io.Buf
import com.twitter.io.Buf.ByteArray.Owned
import com.twitter.util.Duration
import finchtemplate.config.Environment

trait HttpOps {
  val defaultCookieDuration = Duration(10 * 365, TimeUnit.DAYS)
  val distantPast = Duration(-10 * 365, TimeUnit.DAYS)
  val jsonMimeType = "application/json"

  def cookie(k: String, v: String): Cookie = cookie(k, v, defaultCookieDuration)

  def removeCookie(k: String): Cookie = cookie(k, "", distantPast)

  // TODO Add in a domain here when running in production with a real domain.
  def cookie(k: String, v: String, age: Duration): Cookie = {
    val c = new Cookie(k, v)
    c.path = "/"
    c.httpOnly = true
    c.isSecure = !Environment.env.isDevelopment
    c.maxAge = age
    c
  }

  def formEncode(content: (String, Any)*): Buf =
    Owned(content.map(kv => s"${kv._1}=${kv._2.toString}").mkString("&").getBytes(UTF_8))
}

object HttpOps extends HttpOps
