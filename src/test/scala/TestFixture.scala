import zio.ZIO.succeed
import zio.macros.accessible
import zio.{Task, TaskLayer, ZIO, ZLayer, durationInt}


@accessible
trait TestFixture {
  def querySlow(sql: String) : Task[List[List[String]]]
  def query(sql: String) : Task[List[List[String]]]
}

object TestFixture{

  def `in memory with REAL warehouse`(testData: TestData): TaskLayer[TestFixture] =
    (
      MessageBus.fromDocker ++ Directory.inMemory(testData) >+>
        (
          Connectivity.forTestWarehouse(testData) ++
            Brain.inMemory ++
            Combinator.inMemory
          )
      ) >>>
      ZLayer.fromFunction(new TestFixtureDemo(_))

  def `in memory with EMBEDDED warehouse`(testData: TestData) : TaskLayer[TestFixture] =
    ZLayer.make[TestFixture](
      MessageBus.fromDocker,
      Connectivity.inProcess(testData),
      Directory.inMemory(testData),
      Brain.inMemory,
      Combinator.inMemory,
      ZLayer.fromFunction(new TestFixtureDemo(_))
    )

  private class TestFixtureDemo(bus: MessageBus) extends TestFixture {
    private val DEMO_RESULT = List(
      List("Lisbon", "20-30", "2.7"),
      List("Lisbon", "30-40", "4.6"),
      List("Warsaw", "20-30", "2.8"),
      List("Warsaw", "30-40", "4.1")
    )

    def querySlow(sql: String) : Task[List[List[String]]] = ZIO.sleep(3.seconds) *> succeed(DEMO_RESULT)
    def query(sql: String) : Task[List[List[String]]] = succeed(DEMO_RESULT)
  }
}
