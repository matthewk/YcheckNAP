package state

import domain.responses._
import io.lemonlabs.uri.Uri
import javax.inject._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class InternalState {
  @volatile private var status =
    Status("0.1",Uri.parse("http://localhost/runbook"), "7260039 milliseconds")
  @volatile private var health = Health(None)
  @volatile private var gtg: Option[GoodToGo] = Some(GoodToGo("OK"))
  @volatile private var canary: Option[Canary] = Some(Canary("OK"))

  def setStatus(newStatus: Status) = status = newStatus
  def getStatus = Future { status }

  def setHealth(newHealth: Health) = health = newHealth
  def getHealth = Future { health }

  def setGtG(newGtG: Option[GoodToGo]) = gtg = newGtG
  def getGtG = Future { gtg }

  def setCanary(newCanary: Option[Canary]) = canary = newCanary
  def getCanary = Future { canary }

  def getConfig() = Future { Config() }
}
