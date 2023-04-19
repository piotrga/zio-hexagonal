import zio.durationInt
import zio.test.junit.JUnitRunnableSpec
import zio.test.{Spec, TestClock, suite, test}


object QuerySpec extends ShouldAssertions{
  def spec: Spec[TestFixture, Throwable] = {
    suite("Basic queries")(

      test("simple query"){
        TestFixture.query("select * from small_table limit 0")
          .shouldBe(Nil)
      },

      test("illustrate shutdown"){
        (for {
          fiber <- TestFixture.querySlow("select * from LARGE_table limit 0").fork
          _ <- TestClock.adjust(3.second)
          rows <- fiber.join
        } yield rows)
          .shouldBe(Nil)
      }
    )
  }

  def testData : TestData = TestData(/*...*/)
}


class RealWarehouseQueryTest extends JUnitRunnableSpec {
  override def spec =
    QuerySpec.spec
      .provide(TestFixture.inMemoryWithRealWarehouse(QuerySpec.testData))
}

class InMemoryWarehouseQueryTest extends JUnitRunnableSpec {
  override def spec =
    QuerySpec.spec
      .provide(TestFixture.inMemoryWithInMemoryWarehouse(QuerySpec.testData))
}



