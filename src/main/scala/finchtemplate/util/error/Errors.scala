package finchtemplate.util.error

import com.twitter.finagle.http.Response
import io.finch.{Error => FinchError}

abstract class FinchTemplateError extends FinchError

final case class GenericError(reason: String, cause: Option[Throwable]) extends FinchTemplateError {
  override def getMessage: String = reason

  override def getCause: Throwable = cause.orNull
}

final case class AuthenticationFailedError(reason: String, cause: Option[Throwable] = None) extends FinchTemplateError {
  override def getMessage: String = reason

  override def getCause: Throwable = cause.orNull
}

final case class NotFoundError(reason: String, cause: Option[Throwable] = None) extends FinchTemplateError {
  override def getMessage: String = reason

  override def getCause: Throwable = cause.orNull
}

final case class TooManyRedirectsError(redirects: Int) extends FinchTemplateError {
  override def getMessage: String = s"Too many redirects ($redirects)"
}

abstract class UpstreamError(val response: Response) extends FinchTemplateError

final case class UpstreamAuthenticationError(override val response: Response) extends UpstreamError(response) {
  override def getMessage: String = "Client error (authentication failed) while talking to upstream server"
}

final case class UpstreamClientError(override val response: Response) extends UpstreamError(response) {
  override def getMessage: String = "Client error (bad request) while talking to upstream server"
}

final case class UpstreamServerError(override val response: Response) extends UpstreamError(response) {
  override def getMessage: String = "Server error while talking to upstream server"
}

object Errors {
  def error(message: String): FinchTemplateError = GenericError(message, None)

  def error(message: String, cause: Throwable): FinchTemplateError = GenericError(message, Some(cause))

  def authFailedError(message: String): FinchTemplateError = AuthenticationFailedError(message, None)

  def authFailedError(message: String, cause: Throwable): FinchTemplateError = AuthenticationFailedError(message, Some(cause))

  def tooManyRedirects(redirects: Int): FinchTemplateError = TooManyRedirectsError(redirects)

  def upstreamAuthenticationError(response: Response): FinchTemplateError = UpstreamAuthenticationError(response)

  def notFoundError(message: String): FinchTemplateError = NotFoundError(message, None)

  def notFoundError(message: String, cause: Throwable): FinchTemplateError = NotFoundError(message, Some(cause))

  def upstreamClientError(response: Response): FinchTemplateError = UpstreamClientError(response)

  def upstreamServerError(response: Response): FinchTemplateError = UpstreamServerError(response)
}
