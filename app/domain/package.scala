package domain

import io.lemonlabs.uri.Uri
import com.google.inject.Inject
import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}
import play.api.Configuration
import play.api.libs.json._

import scala.util.Try

object responses {
  case class Status(
                   artifactId: String,
                   runbookUri: Uri,
                   upDuration: String
                   )

  object Status {
    implicit val uriFormat = new Format[Uri] {
      override def writes(o: Uri): JsValue = JsString(o.toString)
      override def reads(json: JsValue): JsResult[Uri] = json match {
        case JsString(v)  => Try(JsSuccess(Uri.parse(v))).getOrElse(JsError("couldn't parse"))
        case _            => JsError("didn't match")
      }
    }
    implicit val statusFormat: Format[Status] = Json.format[Status]
  }

  case class Health(failures: Option[List[String]])

  object Health {
    implicit val healthFormat = Json.format[Health]
  }

  case class GoodToGo(status: String)

  object GoodToGo {
    implicit val gtgFormat = Json.format[GoodToGo]
  }

  case class Canary(status: String)

  object Canary {
    implicit val canaryFormat = Json.format[Canary]
  }

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
