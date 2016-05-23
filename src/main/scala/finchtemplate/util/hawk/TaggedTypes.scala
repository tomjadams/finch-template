package finchtemplate.util.hawk

import shapeless.tag
import shapeless.tag._

trait KeyIdTag

trait KeyTag

trait NonceTag

trait PayloadHashTag

trait ExtendedDataTag

trait MACTag

trait RawAuthenticationHeaderTag

trait HeaderKeyValueTag

trait HeaderKeyTag

trait HeaderValueTag

object TaggedTypesFunctions {
  def KeyId(s: String): @@[String, KeyIdTag] = tag[KeyIdTag](s)

  def Key(s: String): @@[String, KeyTag] = tag[KeyTag](s)

  def Nonce(s: String): @@[String, NonceTag] = tag[NonceTag](s)

  def PayloadHash(s: String): @@[String, PayloadHashTag] = tag[PayloadHashTag](s)

  def ExtendedData(s: String): @@[String, ExtendedDataTag] = tag[ExtendedDataTag](s)

  def Mac(s: String): @@[String, MACTag] = tag[MACTag](s)

  def RawAuthenticationHeader(s: String) = tag[RawAuthenticationHeaderTag](s)

  def HeaderKeyValue(s: String) = tag[HeaderKeyValueTag](s)

  def HeaderKey(s: String) = tag[HeaderKeyTag](s)

  def HeaderValue(s: String) = tag[HeaderValueTag](s)
}
