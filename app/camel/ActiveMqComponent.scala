package camel

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import com.typesafe.config.Config
import org.apache.activemq.camel.component.ActiveMQComponent
import org.apache.activemq.pool.PooledConnectionFactory

class ActiveMqComponent(val activeMqPoolName: String)(implicit val system: ActorSystem) extends ActiveMQComponent with CamelComponent with ActiveMqConnectionPool {
  override val name: String = "activemq"

  override val config: Config = system.settings.config

  override val log: LoggingAdapter = Logging(system, this.getClass)

  setConnectionFactory(new PooledConnectionFactory(activeMqConnectionFactory))
}

