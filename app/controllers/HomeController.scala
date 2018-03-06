package controllers

import javax.inject._

import io.kanaka.monadic.dsl._
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

/**
  * A very small controller that renders a home page.
  */
class HomeController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index = Action.async {
    req =>
      for {
        i <- Future.successful(Right(42)) ?| NotFound
      } yield Ok(views.html.index(i))
  }

}
