package finchtemplate.util.hawk.validate

import cats.data.Xor
import com.github.benhutchison.mouse.all._
import finchtemplate.util.hawk._
import finchtemplate.util.hawk.params.RequestContext
import finchtemplate.util.hawk.validate.Maccer.requestMac

object MacValidation {
  def validateMac(credentials: Credentials, context: RequestContext, method: ValidationMethod = PayloadValidationMethod): Xor[Error, RequestValid] =
    requestMac(credentials, context, method).map {
      computedMac => validateMac(computedMac, context.clientAuthHeader.mac)
    }.getOrElse(errorXor("Request MAC does not match computed MAC"))

  private def validateMac(computedMac: MAC, providedMac: MAC): Xor[Error, RequestValid] =
    (computedMac == providedMac).xor(error("Request MAC does not match computed MAC"), new RequestValid {})
}
