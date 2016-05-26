package finchtemplate.util

import shapeless.tag.@@

package object hawk {
  type KeyId = String @@ KeyIdTag
  type Key = String @@ KeyTag
  type Nonce = String @@ NonceTag
  type PayloadHash = String @@ PayloadHashTag
  type ExtendedData = String @@ ExtendedDataTag
  type MAC = String @@ MACTag
  type RawAuthenticationHeader = String @@ RawAuthenticationHeaderTag
  type HeaderKeyValue = String @@ HeaderKeyValueTag
  type HeaderKey = String @@ HeaderKeyTag
  type HeaderValue = String @@ HeaderValueTag

  val MustAuthenticateHttpHeader = "WWW-Authenticate"
  val AuthorisationHttpHeader = "Authorization"
  val HawkHeaderValuePrefix = "Hawk"
  val HawkVersionHeader = "hawk.1.header"
  val HawkVersionPayload = "hawk.1.payload"
}
