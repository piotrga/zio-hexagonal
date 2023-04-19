import zio.ZIO.succeed
import zio.macros.accessible
import zio.{Task, TaskLayer, ZIO, ZLayer, durationInt}


@accessible
trait TestFixture {
  def querySlow(sql: String) : Task[List[List[String]]]
  def query(sql: String) : Task[List[List[String]]]
}

object TestFixture{

  def inMemoryWithInMemoryWarehouse(testData: TestData) : TaskLayer[TestFixture] = ZLayer.make[TestFixture](
    MessageBus.fromDocker,
    Connectivity.inProcess(testData),
    Brain.inMemory,
    Combinator.inMemory,
    ZLayer.fromFunction(new TestFixtureReal(_))
  )

  def inMemoryWithRealWarehouse(testData: TestData): TaskLayer[TestFixture] =
    (
      MessageBus.fromDocker >+>
        (
          Connectivity.forTestWarehouse(testData) ++
            Brain.inMemory ++
            Combinator.inMemory
          )
      ) >>>
      ZLayer.fromFunction(new TestFixtureReal(_))

  private class TestFixtureReal(bus: MessageBus) extends TestFixture {
    def querySlow(sql: String) : Task[List[List[String]]] = ZIO.sleep(3.seconds) *> succeed(Nil)
    def query(sql: String) : Task[List[List[String]]] = succeed(Nil)
  }
}
