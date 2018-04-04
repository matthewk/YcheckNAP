
import domain.responses.{Canary, GoodToGo, Health, Status}
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import io.lemonlabs.uri.Uri
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import state.InternalState
import play.api.inject.bind

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class ApiControllerSpec extends PlaySpec with GuiceOneAppPerTest {

  "ApiController" should {

    "return the status when GET /service/status is invoked" in {
      val request = FakeRequest(GET, "/service/status")
      val controller: Future[Result] = route(app, request).get

      contentAsJson(controller) must be (
        Json.toJson(Status("0.1",Uri.parse("http://localhost/runbook"), "7260039 milliseconds")))
    }

    val is = new InternalState()
    is.setStatus(null)
    def applicationNoState = new GuiceApplicationBuilder()
      .overrides(bind[InternalState].toInstance(is))
      .build()

    "return 500 if the state component is dead" in {
      val request = FakeRequest(GET, "/service/status")
      val controller: Future[Result] = route(applicationNoState, request).get

      val r = Await.result(controller, Duration.Inf)
      assert(r.header.status == 500)
    }

    "set a new status when POST /service/status is invoked" in {
      val requestPost = FakeRequest(POST, "/service/status").withJsonBody(
        Json.parse(
          """{
            |"artifactId": "0.2",
            |"runbookUri": "http://otherhost/runbook",
            |"upDuration": "8954322 milliseconds"
            |}""".stripMargin)
      )

      val requestGet = FakeRequest(GET, "/service/status")

      val res: Future[Result] = for {
        _ <- route(app, requestPost).get
        newStatus <- route(app, requestGet).get
      } yield newStatus

      contentAsJson(res) must be (
        Json.toJson(Status("0.2",Uri.parse("http://otherhost/runbook"), "8954322 milliseconds"))
      )
    }

    "get the health when GET /service/health is invoked" in {
      val request = FakeRequest(GET, "/service/health")
      val controller: Future[Result] = route(app, request).get

      contentAsJson(controller) must be (
        Json.toJson(Health(None))
      )
    }

    "get a health with errors when POST /service/health with errors populated" in {
      val requestPost = FakeRequest(POST, "/service/health").withJsonBody(
        Json.parse(
          """{
            |"failures": [
            | "database failure",
            | "third-party dependency failure"
            | ]
            |}""".stripMargin)
      )

      val requestGet = FakeRequest(GET, "/service/health")

      val res: Future[Result] = for {
        _ <- route(app, requestPost).get
        newStatus <- route(app, requestGet).get
      } yield newStatus

      contentAsJson(res) must be (
        Json.toJson(Health(Some(List("database failure","third-party dependency failure"))))
      )
    }

    "get the GoodToGo when GET /service/gtg is invoked" in {
      val request = FakeRequest(GET, "/service/gtg")
      val controller: Future[Result] = route(app, request).get

      contentAsJson(controller) must be (
        Json.toJson(Some(GoodToGo("OK")))
      )
    }

    "get a 500 when POST /service/gtg with empty body and then GET" in {
      val requestPost = FakeRequest(POST, "/service/gtg").withJsonBody(
        Json.parse(
          """{}""".stripMargin)
      )

      val requestGet = FakeRequest(GET, "/service/gtg")

      val post = route(app, requestPost).get
      contentAsJson(post) must be (
        Json.parse("""{"status":"OK","message":"Empty GtG saved"}""")
      )

      val r = Await.result(route(app, requestGet).get, Duration.Inf)
      assert(r.header.status == 500)
    }

    "get the Canary when GET /service/alive in invoked" in {
      val request = FakeRequest(GET, "/service/alive")
      val controller: Future[Result] = route(app, request).get

      contentAsJson(controller) must be (
        Json.toJson(Some(Canary("OK")))
      )
    }

    "get a 500 when POST /service/alive with empty body and then GET" in {
      val requestPost = FakeRequest(POST, "/service/alive").withJsonBody(
        Json.parse(
          """{}""".stripMargin)
      )

      val requestGet = FakeRequest(GET, "/service/alive")

      val post = route(app, requestPost).get
      contentAsJson(post) must be (
        Json.parse("""{"status":"OK","message":"Empty Canary saved"}""")
      )

      val r = Await.result(route(app, requestGet).get, Duration.Inf)
      assert(r.header.status == 500)
    }

    "get 200 when GET /service/config in invoked" in {
      val request = FakeRequest(GET, "/service/config")
      val controller: Future[Result] = route(app, request).get
      val r = Await.result(controller, Duration.Inf)
      assert(r.header.status == 200)
    }

  }

}