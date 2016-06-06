package finchtemplate.util.time

import com.github.benhutchison.mouse.all._
import com.twitter.util.Try
import finchtemplate.util.log.Logger
import finchtemplate.util.time.TaggedTypesFunctions.{Millis, Seconds}
import finchtemplate.util.time.Time.iso8601Formatter
import org.joda.time.DateTimeZone._
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeConstants, Duration}

object Time {
  private val iso8601Formatter = ISODateTimeFormat.dateTime()
  private[finchtemplate] val iso8601Parser = ISODateTimeFormat.dateTimeParser.withOffsetParsed()
  val millisInSecond: Long = DateTimeConstants.MILLIS_PER_SECOND.toLong

  def time(dateTime: DateTime): Time = Time(Millis(dateTime.getMillis))

  def time(seconds: Seconds): Time = {
    Logger.log.info(s"seconds : $seconds")
    val t = Time(Millis(seconds * millisInSecond))
    Logger.log.info(s"parsed time: $t")
    t
  }

  def utcTime(millis: Millis): Time = time(new DateTime(millis, UTC))

  def nowUtc: Time = time(DateTime.now(UTC))

  def parseIsoAsTime(iso8601: String): Option[Time] = Try(DateTime.parse(iso8601, iso8601Parser)).map(time(_)).toOption

  def parseMillisAsTimeUtc(millis: String): Option[Time] = millis.parseLongOption.map((m: Long) => Time(Millis(m)))

  def parseSecondsAsTimeUtc(seconds: String): Option[Time] = seconds.parseLongOption.map(s => time(Seconds(s)))

  def secondsToMillis(seconds: Seconds): Millis = Millis(seconds * millisInSecond)

  def millisToSeconds(millis: Millis): Seconds = Seconds((millis / millisInSecond).toInt.toLong)
}

final case class Time(millis: Millis) {
  def minus(time: Time): Duration = Duration.millis(this.millis - time.millis)

  def asDateTime: DateTime = new DateTime(millis)

  def asSeconds: Seconds = Seconds(Time.millisToSeconds(millis))

  def toIso8601: String = iso8601Formatter.print(millis)
}
