package finchtemplate.util.hawk.parse

/**
  * A moderately strict authentication header key/value parser that doesn't try overly hard to produce correct key/value
  * pairs. The only real thing it handles is spaces. So does not like things double commas, extra fields, etc.
  */
object HeaderKeyValueParser {
  val doubleQuote = '"'

  def parse(kv: HeaderKeyValue): Option[Map[HeaderKey, HeaderValue]] =
    if (kv.count(_ == '=') == 1) {
      val kvs = kv.split("=")
      if (kvs.length == 2) {
        val key = kvs(0).trim
        val value = kvs(1).trim
        if (keyValid(key) && valueValid(value)) {
          Some(Map(key -> stripQuotes(value)))
        } else {
          None
        }
      } else {
        None
      }
    } else {
      None
    }

  private def keyValid(key: HeaderKey): Boolean = {
    key.count(_ == doubleQuote) == 0 && key.matches("[a-zA-Z]")
  }

  private def valueValid(value: HeaderValue): Boolean =
    value.startsWith(doubleQuote.toString) && value.endsWith(doubleQuote.toString) && value.count(_ == doubleQuote) == 2

  private def stripQuotes(s: HeaderValue): HeaderValue = {
    s
  }
}
