package finchtemplate.util.hawk.validate

import cats.data.Xor
import com.github.benhutchison.mouse.all._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.hawk.validate.Maccer.requestMac

trait MacValid

object MacValidation extends Validator[MacValid] {
  override def validate(credentials: Credentials, context: RequestContext, method: ValidationMethod): Xor[HawkError, MacValid] =
    requestMac(credentials, context, method).map {
      computedMac => validateMac(computedMac, context.clientAuthHeader.mac)
    }.getOrElse(errorXor("Request MAC does not match computed MAC"))

  private def validateMac(computedMac: MAC, providedMac: MAC): Xor[HawkError, MacValid] =
    (computedMac == providedMac).xor(error("Request MAC does not match computed MAC"), new MacValid {})
}
