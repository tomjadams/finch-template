package finchtemplate.util.time

import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC
import org.joda.time.format.ISODateTimeFormat

import scala.util.control.NonFatal

trait TimeOps {
  private val iso8601Formatter = ISODateTimeFormat.dateTime()
  private[finchtemplate] val iso8601Parser = ISODateTimeFormat.dateTimeParser.withOffsetParsed()

  def parseIsoAsTime(iso8601: String): Option[DateTime] = try {
    Some(DateTime.parse(iso8601, iso8601Parser))
  } catch {
    case NonFatal(t) => None
  }

  def parseMillisAsTime(millis: String): Option[DateTime] = try {
    Some(new DateTime(millis.toLong))
  } catch {
    case NonFatal(t) => None
  }

  def toIso8601(millis: Millis): String = toIso8601(new DateTime(millis, UTC))

  def toIso8601(time: DateTime): String = iso8601Formatter.print(time)
}

object TimeOps extends TimeOps
