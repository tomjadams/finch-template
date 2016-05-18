package finchtemplate.auth

final case class AuthToken(private val token: Uuid) {
  def asSessionId: String = token.uuid
}

object AuthToken {
  def generate: AuthToken = AuthToken(Uuid.generate)

  def authToken(token: String): AuthToken = AuthToken(Uuid(token))
}
