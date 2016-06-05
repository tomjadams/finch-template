# Basic Code Conventions

## Syntax

This is a good start: https://github.com/databricks/scala-style-guide

## Specifics

* Prefer immutability.
* Prefer explicitness over implicitness. It makes code easier to reason about, removes magic & increases compile times.
* Even though `Future` has its own "eitherness", use `Future.exception` only for exceptional things (i.e. where you'd
  throw an exception). Use an `Option` for things that may or may not be there, and `Either` or `Xor` for things that
  may fail, but where that failure is something you want to handle yourself (rather than letting bubble up), even if
  that computation is itself wrapped in a future, e.g. `Future[Option[Int]]`. Here is some
  [more information](http://eed3si9n.com/herding-cats/stacking-future-and-either.html) on solving the issues that crop
  up doing this.
* Use strong types for things, rather than `String` or `Long`, e.g. `final case class Foo(foo: Long)`. You can also type
alias them. Tagged types in Shapeless are good for this too.
* Use final on all classes unless you've designed them to be inherited from.

## JSON encoding

* When returning an "entity" through the API, always return an identifier for the thing, in case a client needs to cache it.
* If you are returning the same object through the API, use the same structure where possible, rather than trying to
simplify/change things. e.g. prefer `{"location":{"name":"foo", "latitide":100.0, ...}}` for details, and
`{"location":{"name":"foo"}}` for summary (rather than `{"location":"foo"}`).

