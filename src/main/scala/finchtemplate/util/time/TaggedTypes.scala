package finchtemplate.util.time

import shapeless.tag
import shapeless.tag.@@

trait MillisTag

object TaggedTypesFunctions {
  def Millis(l: Long): @@[Long, MillisTag] = tag[MillisTag](l)
}
