package finchtemplate.http

import com.twitter.finagle.http.{Request, Response, Status}
import finchtemplate.util.json.JsonCodecOps
import io.finch.EncodeResponse

trait ResponseOps extends JsonCodecOps {
  def jsonResponse[A, R <: Request](request: R, status: Status, a: A)(implicit encoder: EncodeResponse[A]): Response = {
    val response = request.response
    response.status = status
    response.setContentTypeJson()
    response.contentString = jsonString(a)
    response
  }
}

object ResponseOps extends ResponseOps
