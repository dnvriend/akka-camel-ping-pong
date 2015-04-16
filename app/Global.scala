
import camel.{ActiveMqComponent, CamelHelper}
import play.api.{Application, GlobalSettings}
import play.libs.Akka

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    implicit val system = Akka.system
    CamelHelper.addComponent(new ActiveMqComponent("broker1"))
  }
}
