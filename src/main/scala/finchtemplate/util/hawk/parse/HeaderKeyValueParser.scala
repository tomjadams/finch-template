package finchtemplate.util.hawk.parse

import scala.util.matching.Regex

/**
  * A reasonably liberal authentication header key/value parser that doesn't try overly hard to produce correct key/value
  * pairs.
  */
object HeaderKeyValueParser {
  private val keyRegex = new Regex("""([_a-zA-Z]+?[_a-zA-Z0-9]{1,256})\s*=""", "key")
  private val valueRegex = new Regex("""\"(.*)\"""", "value")

  def parseKeyValue(kv: HeaderKeyValue): Option[Map[HeaderKey, HeaderValue]] = {
    val key = keyRegex.findFirstMatchIn(kv).map { m => m.group("key") }
    val value = valueRegex.findFirstMatchIn(kv).map { m => m.group("value") }
    for (x <- key; y <- value) yield Map(x.trim -> y.trim)
  }
}
