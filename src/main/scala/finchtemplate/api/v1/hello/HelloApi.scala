package finchtemplate.api.v1.hello

import finchtemplate.util.http.HttpOps
import finchtemplate.util.log.Logging
import io.finch.{Endpoint, _}

object HelloApi extends HttpOps with Logging {
  def helloApi() = hello

  def hello: Endpoint[Hello] =
    get("v1" :: "hello" :: string("name")) { (name: String) =>
      Ok(Hello(name))
    }
}
