package finchtemplate.util.hawk.validate

import cats.data.Xor
import cats.data.Xor._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.time.TaggedTypesFunctions._
import finchtemplate.util.time.TimeOps
import org.joda.time.{DateTime, Duration}

trait TimeValid

object TimeValidation extends Validator[TimeValid] {
  val acceptableTimeDelta = Duration.standardMinutes(2)

  override def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[Error, TimeValid] = {
    val delta = Millis(math.abs(TimeOps.nowUtc.getMillis - clientTs(context).getMillis))
    if (delta <= acceptableTimeDelta.getMillis) {
      right(new TimeValid {})
    } else {
      errorXor("Timestamp invalid")
    }
  }

  private def clientTs(context: RequestContext): DateTime = TimeOps.utcTime(context.clientAuthHeader.timestamp)
}
