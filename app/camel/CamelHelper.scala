package camel

import akka.actor.ActorSystem
import akka.camel.{Camel, CamelExtension}
import akka.event.Logging
import org.apache.camel.{Component, CamelContext}

object CamelHelper {
  def apply(system: ActorSystem): Camel = CamelExtension(system)

  def apply(camel: Camel): CamelContext = camel.context

  def addComponent(camelComponent: CamelComponent)(implicit system: ActorSystem): CamelContext = {
    val camelContext = CamelExtension(system).context
    val log = Logging(system, this.getClass)
    Option(camelContext.getComponent(camelComponent.name)).map { comp =>
      camelContext.removeComponent(camelComponent.name)
      camelContext.addComponent(camelComponent.name, getComponent(camelComponent))
      log.info(s"Found camel component '${camelComponent.name}' in the CamelContext, removing old component one and adding a new configured instance")
    }.getOrElse {
      log.info(s"No camel component ${camelComponent.name}, found, adding a new instance")
      camelContext.addComponent(camelComponent.name, getComponent(camelComponent))
    }
    camelContext
  }

  private def getComponent(component: CamelComponent): Component = component match {
    case toTest if toTest.isInstanceOf[Component] => toTest.asInstanceOf[Component]
    case _ => throw new IllegalArgumentException("wrong type")
  }
}