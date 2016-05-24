package finchtemplate.util.time

import com.twitter.util._
import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC
import org.joda.time.format.ISODateTimeFormat

trait TimeOps {
  private val iso8601Formatter = ISODateTimeFormat.dateTime()
  private[finchtemplate] val iso8601Parser = ISODateTimeFormat.dateTimeParser.withOffsetParsed()

  def parseIsoAsTime(iso8601: String): Option[DateTime] = Try(DateTime.parse(iso8601, iso8601Parser)).toOption

  def parseMillisAsTime(millis: String): Option[DateTime] = Try(new DateTime(millis.toLong)).toOption

  def toIso8601(millis: Millis): String = toIso8601(new DateTime(millis, UTC))

  def toIso8601(time: DateTime): String = iso8601Formatter.print(time)
}

object TimeOps extends TimeOps
