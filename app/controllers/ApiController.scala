package controllers

import domain.responses.{Canary, Config, GoodToGo, Routes}
import javax.inject._
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import state.InternalState

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * A very small controller that renders the endpoints.
  */
class ApiController @Inject()
  (cc: ControllerComponents, state: InternalState, routes: Routes)
  (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index = Action.async {
    req => {
      for {
        i <- routes.getRoutes(req.path)
      } yield i.toString()
    }.transformWith {
      case Success(i) => Future.successful(Ok(i))
      case Failure(_) => Future.successful(InternalServerError)
    }
  }

  def status = Action.async {
    req => {
      for {
        i <- state.getStatus
      } yield Json.toJson(i)
    }.transformWith {
      case Success(i) => Future.successful(Ok(i))
      case Failure(_) => Future.successful(InternalServerError)
    }
  }

  def statusPost = Action.async(parse.json) {
    req => {
      val reqJs = req.body.validate[domain.responses.Status]

      val res = reqJs.fold(
        errors => {
          BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors)))
        },
        stat => {
          state.setStatus(stat)
          Ok(Json.obj("status" ->"OK", "message" -> ("New status saved.") ))
        }
      )
      Future.successful(res)
    }
  }

  def healthcheck = Action.async {
    req => {
      for {
        i <- state.getHealth
      } yield Json.toJson(i)
    }.transformWith {
      case Success(i) => Future.successful(Ok(i))
      case Failure(_) => Future.successful(InternalServerError)
    }
  }

  def healthcheckPost = Action.async(parse.json) {
    req => {
      val reqJs = req.body.validate[domain.responses.Health]

      val res = reqJs.fold(
        errors => {
          BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors)))
        },
        health => {
          state.setHealth(health)
          Ok(Json.obj("status" ->"OK", "message" -> ("New health saved.") ))
        }
      )
      Future.successful(res)
    }
  }

  def goodToGo = Action.async {
    _ => {
      for {
        i <- state.getGtG
      } yield i
    }.map {
        case Some(j) => Ok(Json.toJson(j))
        case None => InternalServerError
    }
  }

  def goodToGoPost = Action.async(parse.json) {
    req => {
      Future {
        req.body.validate[domain.responses.GoodToGo] match {
          case s: JsSuccess[GoodToGo] => {
            state.setGtG(Some(s.get))
            Json.obj("status" -> "OK", "message" -> ("New GtG saved."))
          }
          case e: JsError =>
            state.setGtG(None)
            Json.obj("status" -> "OK", "message" -> ("Empty GtG saved"))
        }
      }.transformWith { r =>
        Future.successful(r match {
          case Success(s) => Ok(s)
          case Failure(_) => InternalServerError
        })
      }
    }
  }


  def alive = Action.async {
    _ => {
      for {
        i <- state.getCanary
      } yield i
    }.map {
      case Some(j) => Ok(Json.toJson(j))
      case None => InternalServerError
    }
  }

  def alivePost = Action.async(parse.json) {
    req => {
      Future {
        req.body.validate[domain.responses.Canary] match {
          case s: JsSuccess[Canary] => {
            state.setCanary(Some(s.get))
            Json.obj("status" -> "OK", "message" -> ("New Canary saved."))
          }
          case e: JsError =>
            state.setCanary(None)
            Json.obj("status" -> "OK", "message" -> ("Empty Canary saved"))
        }
      }.transformWith { r =>
        Future.successful(r match {
          case Success(s) => Ok(s)
          case Failure(_) => InternalServerError
        })
      }
    }
  }

  def config = Action.async {
    _ => {
      for {
        i <- state.getConfig
      } yield i
    }.map {
      case c: Config => Ok(Json.toJson(c))
      case _ => InternalServerError
    }
  }

}
