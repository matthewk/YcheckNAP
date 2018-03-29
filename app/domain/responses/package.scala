package domain

import io.lemonlabs.uri.Uri

import com.google.inject.Inject
import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}
import play.api.Configuration
import play.api.libs.json.{JsString, JsValue, Writes}

package object responses {
  case class Status(
                   artifactId: String,
                   runbookUri: Uri,
                   upDuration: String
                   )

  case class GoodToGo(status: String)

  case class Canary(status: String)

  case class Config @Inject() (
                   config: Configuration
                   )

  object Config {
    implicit val g: Writes[Config] = new Writes[Config] {
      override def writes(o: Config): JsValue =
        JsString(ConfigFactory.load().root().render(ConfigRenderOptions.concise()))
    }
  }
}
