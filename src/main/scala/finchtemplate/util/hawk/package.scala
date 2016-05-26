package finchtemplate.util

import shapeless.tag.@@

package object hawk {
  type KeyId = String @@ KeyIdTag
  type Key = String @@ KeyTag
  type Nonce = String @@ NonceTag
  type PayloadHash = String @@ PayloadHashTag
  type ExtendedData = String @@ ExtendedDataTag
  type RawAuthenticationHeader = String @@ RawAuthenticationHeaderTag
  type HeaderKeyValue = String @@ HeaderKeyValueTag
  type HeaderKey = String @@ HeaderKeyTag
  type HeaderValue = String @@ HeaderValueTag
  type Base64Encoded = String @@ Base64EncodedTag

  val MustAuthenticateHttpHeader = "WWW-Authenticate"
  val AuthorisationHttpHeader = "Authorization"
  val HawkHeaderValuePrefix = "Hawk"
}
