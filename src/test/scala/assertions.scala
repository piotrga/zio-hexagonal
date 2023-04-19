import zio.ZIO
import zio.test.Assertion.{equalTo, fails, isSubtype}
import zio.test.{Assertion, TestResult, assertZIO}

import scala.reflect.ClassTag

trait ShouldAssertions{
  implicit class ZIOAssertionsShouldExtension[R, E, T](x: ZIO[R, E, T]) {
    def should(a: Assertion[T]): ZIO[R, E, TestResult] = assertZIO(x)(a)
    def shouldBe(expected: T): ZIO[R, E, TestResult] = assertZIO(x)(equalTo(expected))
    def shouldFailWith[E1 <: E](a: Assertion[E1])(implicit e: ClassTag[E1]): ZIO[R, E, TestResult] = assertZIO(x.exit)(fails(isSubtype[E1](a)))
  }
}