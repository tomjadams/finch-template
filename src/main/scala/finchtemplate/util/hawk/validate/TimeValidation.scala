package finchtemplate.util.hawk.validate

import cats.data.Xor
import com.github.benhutchison.mouse.all._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.time.Time.nowUtc
import org.joda.time.Duration

trait TimeValid

object TimeValidation extends Validator[TimeValid] {
  val acceptableTimeDelta = Duration.standardMinutes(2)

  override def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[HawkError, TimeValid] = {
    val delta = nowUtc.minus(context.clientAuthHeader.timestamp).getStandardSeconds

    //    Logger.log.info(s"TimeOps.nowUtc.getMillis : ${TimeOps.nowUtc.getMillis}")
    //    Logger.log.info(s"clientTs(context).getMillis : ${clientTs(context).getMillis}")

    (delta <= acceptableTimeDelta.getStandardSeconds).xor(error("Timestamp invalid"), new TimeValid {})
  }
}
