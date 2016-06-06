package finchtemplate.util.time

import com.github.benhutchison.mouse.all._
import com.twitter.util._
import finchtemplate.util.time.TaggedTypesFunctions.{Millis, Seconds}
import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC
import org.joda.time.format.ISODateTimeFormat

// TODO TJA Create a time class that enca

trait TimeOps {
  private val iso8601Formatter = ISODateTimeFormat.dateTime()
  private[finchtemplate] val iso8601Parser = ISODateTimeFormat.dateTimeParser.withOffsetParsed()
  val MillisInSecond: Long = 1000L

  def parseIsoAsTime(iso8601: String): Option[DateTime] = Try(DateTime.parse(iso8601, iso8601Parser)).toOption

  def parseMillisAsTime(millis: String): Option[DateTime] = millis.parseLongOption.map(m => new DateTime(m))

  def parseSecondsAsTime(seconds: String): Option[DateTime] = seconds.parseIntOption.map(s => new DateTime(secondsToMillis(Seconds(s))))

  def toIso8601(millis: Millis): String = toIso8601(utcTime(millis))

  def toIso8601(time: DateTime): String = iso8601Formatter.print(time)

  def utcTime(millis: Millis): DateTime = new DateTime(millis, UTC)

  def utcTime(seconds: Seconds): DateTime = new DateTime(seconds * MillisInSecond, UTC)

  def nowUtc: DateTime = DateTime.now(UTC)

  def nowUtcMillis: Millis = Millis(DateTime.now(UTC).getMillis)

  def nowUtcSeconds: Seconds = millisToSeconds(Millis(DateTime.now(UTC).getMillis))

  def millisToSeconds(millis: Millis): Seconds = Seconds((millis / MillisInSecond).toInt)

  def secondsToMillis(seconds: Seconds): Millis = Millis(seconds * MillisInSecond)
}

object TimeOps extends TimeOps
