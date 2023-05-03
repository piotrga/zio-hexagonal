import zio.config.magnolia.{DeriveConfig, deriveConfig}
import zio.config.typesafe.TypesafeConfigProvider
import zio.{Config, Layer, Tag, ZIO, ZIOApp, ZIOAppDefault, ZLayer}

object BrainApp extends ZIOAppDefault with SimpleService {

  override def run =
    Brain.live.launch
      .provideLayer(`classpath:application.conf`[Brain.Config])

}

trait SimpleService{ self : ZIOApp =>
  /* Intentionally not providing config to the entire environment,
   as it is crucial to control what piece of config is consumed where */
  final def `classpath:application.conf`[T: DeriveConfig: Tag]: Layer[Config.Error, T] =
    ZLayer.fromZIO {
      ZIO.config[T](deriveConfig[T])
        .provideLayer(zio.Runtime.setConfigProvider(TypesafeConfigProvider.fromResourcePath()))
    }

}