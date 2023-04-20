import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.{Config, Layer, ZIO, ZIOAppDefault, ZLayer}

object BrainApp extends ZIOAppDefault {

  override val bootstrap =
    `classpath:application.conf` >>> Brain.live

  override def run =
    ZIO.unit.forever

  /* Intentionally not providing config to the entire environment as it is crucial to control what piece of config is consumed where */
  private def `classpath:application.conf`: Layer[Config.Error, Brain.Config] =
    ZLayer.fromZIO {
    ZIO.config[Brain.Config](deriveConfig[Brain.Config])
      .provideLayer(zio.Runtime.setConfigProvider(TypesafeConfigProvider.fromResourcePath()))
  }
}
