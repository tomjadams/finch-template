package finchtemplate.spec

import java.net.URI
import java.security.SecureRandom
import java.util.UUID._

import finchtemplate.auth.{AuthToken, Uuid}
import finchtemplate.spec.NumberGenerator._
import org.joda.time.DateTime

object NumberGenerator {
  val numberGenerator = new SecureRandom
}

trait SimpleObjectStubGenerator {
  def stubLong: Long = numberGenerator.nextLong()

  def stubDateTime: DateTime = new DateTime(stubLong)

  def stubString(prefix: String = "double"): String = s"$prefix-${randomUUID.toString}"

  def stubUri(prefix: String = "double"): URI = new URI(s"http://example.com/$prefix-${randomUUID.toString}")
}

trait DomainObjectStubGenerator extends SimpleObjectStubGenerator {
  def stubAuthToken: AuthToken = AuthToken(Uuid(stubString("auth_token")))
}

trait DataStubGenerator extends SimpleObjectStubGenerator with DomainObjectStubGenerator
