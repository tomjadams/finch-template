package finchtemplate.util.hawk.validate

import cats.data.Xor
import com.github.benhutchison.mouse.all._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.time.TaggedTypesFunctions._
import finchtemplate.util.time.TimeOps
import org.joda.time.{DateTime, Duration}

trait TimeValid

object TimeValidation extends Validator[TimeValid] {
  val acceptableTimeDelta = Duration.standardMinutes(2)

  override def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[HawkError, TimeValid] = {
    val delta = Millis(math.abs(TimeOps.nowUtc.getMillis - clientTs(context).getMillis))
    (delta <= acceptableTimeDelta.getMillis).xor(error("Timestamp invalid"), new TimeValid {})
  }

  private def clientTs(context: RequestContext): DateTime = TimeOps.utcTime(context.clientAuthHeader.timestamp)
}
