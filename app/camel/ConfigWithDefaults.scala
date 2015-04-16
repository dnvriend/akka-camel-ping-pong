package camel

import com.typesafe.config.Config

trait ConfigWithDefaults {
  def getSettingOrDefault[U](config: Config, key: String, default: U): U =
    if(config.hasPath(key)) config.getAnyRef(key).asInstanceOf[U] else default
}
