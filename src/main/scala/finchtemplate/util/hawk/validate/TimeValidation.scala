package finchtemplate.util.hawk.validate

import cats.data.Xor
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.RequestContext
import org.joda.time.Duration

trait TimeValid

object TimeValidation extends Validator[TimeValid] {
  val acceptableTimeDelta = Duration.standardMinutes(2)

  override def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[Error, TimeValid] = {
    errorXor("not implemented")
  }
}
