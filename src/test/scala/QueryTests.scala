import zio.test.TestAspect.withLiveClock
import zio.test.{Spec, ZIOSpecDefault, suite, test}


object QuerySpec extends ShouldAssertions{
  def spec: Spec[TestFixture, Throwable] = {
    suite("Basic queries")(

      test("sick days"){
        TestFixture
          .query("SELECT city, age_range, AVG(sick_days) FROM patients GROUP BY 1,2 ORDER BY 1, 2")
          .shouldBe(List(
            List("Lisbon", "20-30", "2.7"),
            List("Lisbon", "30-40", "4.6"),
            List("Warsaw", "20-30", "2.8"),
            List("Warsaw", "30-40", "4.1")
          ))
      },

      test("some other test..."){
        TestFixture
          .querySlow("select * from LARGE_table limit 0")
          .shouldBe(Nil)
      } @@ withLiveClock
    )
  }

  def testData : TestData =
    TestData(/*...*/)
}


//class RealWarehouseQueryTest extends JUnitRunnableSpec {
//  override def spec = QuerySpec.spec
//      .provideLayerShared(TestFixture.`in memory with REAL warehouse`(QuerySpec.testData))
//}

object EmbeddedWarehouseQueryTest extends ZIOSpecDefault {
  override def spec =
    QuerySpec.spec
      .provideShared(TestFixture.`in memory with EMBEDDED warehouse`(QuerySpec.testData))
}



