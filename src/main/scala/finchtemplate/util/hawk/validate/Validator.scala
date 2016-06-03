package finchtemplate.util.hawk.validate

import cats.data.Xor
import finchtemplate.util.hawk.{HawkError, ValidationMethod}
import finchtemplate.util.hawk.params.RequestContext

trait Validator[T] {
  def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[HawkError, T]
}
