import domain.responses.Status
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
import io.lemonlabs.uri.Uri

class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest {

  "HomeController" should {

    "render the index page" in {
      val request = FakeRequest(GET, "/status").withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home = route(app, request).get

      contentAsString(home) must be (Status("0.1",Uri("http://localhost/runbook"), "uptimeString"))
    }

  }

}