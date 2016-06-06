package finchtemplate.util.hawk.validate

import cats.data.Xor
import com.github.benhutchison.mouse.all._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.time.TaggedTypesFunctions._
import finchtemplate.util.time.{Seconds, _}
import org.joda.time.{DateTime, Duration}

trait TimeValid

object TimeValidation extends Validator[TimeValid] {
  val acceptableTimeDelta = Duration.standardMinutes(2)

  override def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[HawkError, TimeValid] = {
    val delta = Seconds(math.abs(TimeOps.nowUtcSeconds - TimeOps.millisToSeconds(Millis(clientTs(context).getMillis))))

    //    Logger.log.info(s"TimeOps.nowUtc.getMillis : ${TimeOps.nowUtc.getMillis}")
    //    Logger.log.info(s"clientTs(context).getMillis : ${clientTs(context).getMillis}")

    (delta <= acceptableTimeDelta.getStandardSeconds).xor(error("Timestamp invalid"), new TimeValid {})
  }

  private def timeDeltaWithNow(ts: Seconds): Seconds =
    Seconds(math.abs(TimeOps.nowUtcSeconds - TimeOps.millisToSeconds(Millis(TimeOps.utcTime(ts).getMillis))))

  private def clientTs(context: RequestContext): DateTime = TimeOps.utcTime(context.clientAuthHeader.timestamp)
}
