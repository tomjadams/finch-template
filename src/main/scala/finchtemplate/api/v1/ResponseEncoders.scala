package finchtemplate.api.v1

import finchtemplate.api.v1.hello.HelloResponseEncoders
import finchtemplate.util.error.ErrorResponseEncoders

trait ResponseEncoders extends ErrorResponseEncoders with HelloResponseEncoders

object ResponseEncoders extends ResponseEncoders
