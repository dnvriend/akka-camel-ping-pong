package camel

import akka.event.LoggingAdapter
import com.typesafe.config.Config
import org.apache.activemq.ActiveMQConnectionFactory

/**
 * Please create a custom dispatcher for this component
 */
trait ActiveMqConnectionPool extends ConfigWithDefaults with ActiveMqConfig {

  def config: Config

  def log: LoggingAdapter

  def activeMqPoolName: String

  lazy val activeMqPoolSettings = config.getConfig(s"activemq.$activeMqPoolName")

  override lazy val username: String = activeMqPoolSettings.getString("username")

  override lazy val password: String = activeMqPoolSettings.getString("password")

  override lazy val host: String = activeMqPoolSettings.getString("host")

  override lazy val port: Int = activeMqPoolSettings.getInt("port")

  override lazy val maxConnections: Int = activeMqPoolSettings.getInt("maxConnections")

  /**
   * A ConnectionFactory is an an Administered object, and is used for creating Connections.
   * This class also implements QueueConnectionFactory and TopicConnectionFactory. You can use this connection to create
   * both QueueConnections and TopicConnections.
   */
  lazy val activeMqConnectionFactory =  {
    log.debug(s"Loading ActiveMqConnectionPool [$activeMqPoolName] using(username: $username, password: $password, brokerUrl: $brokerUrl)")
    new ActiveMQConnectionFactory(username, password, brokerUrl)
  }
}

