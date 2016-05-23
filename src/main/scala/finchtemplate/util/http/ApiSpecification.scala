package finchtemplate.util.http

import java.net.URL

trait ApiSpecification {
  def scheme: String

  def host: String

  def port: Int

  def absoluteBaseUrl(path: String): URL = new URL(scheme, host, port, path)
}
