package finchtemplate.util.time

import shapeless.tag
import shapeless.tag.@@

trait MillisTag
trait SecondsTag

object TaggedTypesFunctions {
  def Millis(m: Long): @@[Long, MillisTag] = tag[MillisTag](m)
  def Seconds(s: Long): @@[Long, SecondsTag] = tag[SecondsTag](s)
}
