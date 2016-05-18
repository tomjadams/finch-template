package finchtemplate.http

import com.twitter.util.Future
import finchtemplate.auth.AuthToken._
import finchtemplate.auth.Authenticate._
import finchtemplate.auth.AuthenticatedClient
import finchtemplate.util.error.Errors._
import io.finch.Output.payload
import io.finch.{Endpoint, _}

trait OptionalAuthRequestReader {
  val optionalAuthorise: Endpoint[Option[AuthenticatedClient]] =
    headerOption(httpAuthTokenHeader).mapOutputAsync { maybeToken =>
      Future.value(payload(maybeToken.map(t => AuthenticatedClient(authToken(t)))))
    }
}

object OptionalAuthRequestReader extends OptionalAuthRequestReader

trait RequiredAuthRequestReader {
  val authorise: Endpoint[AuthenticatedClient] =
    headerOption(httpAuthTokenHeader).mapOutputAsync { maybeToken =>
      maybeToken.map(t => AuthenticatedClient(authToken(t))) match {
        case Some(c) => authorised(c)
        case None => unauthorized
      }
    }

  private def authorised(c: AuthenticatedClient): Future[Output[AuthenticatedClient]] = Future.value(payload(c))

  private def unauthorized: Future[Output[AuthenticatedClient]] =
    Future.value(Unauthorized(authFailedError(s"Missing auth token; include header '$httpAuthTokenHeader'")))
}

object RequiredAuthRequestReader extends RequiredAuthRequestReader
