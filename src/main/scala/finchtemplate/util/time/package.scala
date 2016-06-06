package finchtemplate.util

import shapeless.tag.@@

package object time {
  type Millis = Long @@ MillisTag
  type Seconds = Long @@ SecondsTag
}
