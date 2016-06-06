package finchtemplate.util.time

import org.joda.time.{DateTime, Duration}

object Time {
  def time(dateTime: DateTime): Time = ???
}

final case class Time(millis: Millis) {

  def delta(time: Time): Duration = ???

}
