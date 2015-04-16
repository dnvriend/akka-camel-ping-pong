package camel

trait ActiveMqConfig {
  def username: String
  def password: String
  def host: String
  def port: Int

  def maxConnections: Int = 8

  lazy val brokerUrl: String = s"tcp://$host:$port"
}
