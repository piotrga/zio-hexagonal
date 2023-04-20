import zio.stream.ZStream
import zio.{TaskLayer, ULayer, ZLayer, durationInt}

trait Directory

object Directory{
  case class Config()
  def inMemory(testData: TestData) : ULayer[Directory] = ZLayer.succeed(new Directory {})
  def apply(config: Config) : TaskLayer[Directory] = ZLayer.succeed(new Directory {})
}

trait Audit

object Audit{
  case class Config()
  def inMemory : ULayer[Audit] = ZLayer.succeed(new Audit {})
  def apply(config: Config) : TaskLayer[Audit] = ZLayer.succeed(new Audit {})

}

trait MessageBus {
  def subscribe(topic: String): ZStream[Any, Nothing, String]
}

object MessageBus{
  case class Config(tickSize: zio.Duration = 1.second)

  def fromDocker : TaskLayer[MessageBus] = apply(Config())

  def apply(config: Config) : TaskLayer[MessageBus] = ZLayer.succeed(new MessageBus {
    override def subscribe(topic: String): ZStream[Any, Nothing, String] =
      ZStream.tick(config.tickSize)
        .as(".")

  })
}

trait Authentication
object Authentication{
  case class Config()
  def fromDocker : TaskLayer[Authentication] = ???
}

trait Connectivity

object Connectivity{
  case class Config()
  private val CONFIG_FOR_TEST_WAREHOUSE = Config(/**/)

  def apply(config: Config) : ZLayer[MessageBus, Throwable, Connectivity] = ZLayer.succeed(new Connectivity {})

  def forTestWarehouse(testData: TestData): ZLayer[MessageBus, Throwable, Connectivity] = apply(CONFIG_FOR_TEST_WAREHOUSE)
  def inProcess(testData: TestData): ZLayer[MessageBus, Throwable, Connectivity] = ZLayer.succeed(new Connectivity {})
}

trait Combinator
object Combinator{
  def inMemory: TaskLayer[Combinator] = ZLayer.succeed(new Combinator {})
}


trait TablePlusData
trait TestData

object TestData{
  def apply(tables: TablePlusData*) : TestData = new TestData{}
}