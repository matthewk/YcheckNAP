package domain

import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}
import io.lemonlabs.uri.Uri
import javax.inject.{Inject, Singleton}
import play.api.inject.RoutesProvider
import play.api.libs.json._

import scala.concurrent.Future
import scala.util.Try

object responses {

  @Singleton
   case class Routes @Inject()(router: RoutesProvider){
    def getRoutes(caller: String): Future[Map[String, Seq[String]]] = Future.successful(router.get.documentation
      .filterNot(k => k._2.equalsIgnoreCase(caller))
      .groupBy(_._2)
      .map{case (path, methods) => path -> methods.map(_._1)})
  }

  object Routes {
//    implicit val routesFormat: Format[Routes] = Json.format[Routes]
  }


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

  case class Config()

  object Config {
    implicit val g: Writes[Config] = new Writes[Config] {
      override def writes(o: Config): JsValue = {
        val cfg = ConfigFactory.parseResources("application.conf")
        Json.parse(cfg.root().render(ConfigRenderOptions.concise()))
      }
    }
  }
}
