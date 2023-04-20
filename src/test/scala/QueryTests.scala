import zio.test.TestAspect.ignore
import zio.test.junit.JUnitRunnableSpec
import zio.test.{Spec, suite, test}


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
      } @@ ignore
    )
  }

  def testData : TestData = TestData(/*...*/)
}


class RealWarehouseQueryTest extends JUnitRunnableSpec {
  override def spec = QuerySpec.spec
      .provide(TestFixture.`in memory with REAL warehouse`(QuerySpec.testData))
}

class EmbeddedWarehouseQueryTest extends JUnitRunnableSpec {
  override def spec =
    QuerySpec.spec
      .provide(TestFixture.`in memory with EMBEDDED warehouse`(QuerySpec.testData))
}



