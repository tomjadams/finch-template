package finchtemplate.util.hawk.validate

import cats.data.Xor
import finchtemplate.util.hawk.ValidationMethod
import finchtemplate.util.hawk.params.RequestContext

trait TimeValid

object TimeValidation extends Validator[TimeValid] {
  override def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[Error, TimeValid] = ???
}
