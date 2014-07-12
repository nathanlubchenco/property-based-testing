import org.specs2.mutable._
import org.specs2.ScalaCheck
import org.scalacheck._

case class Even(i: Int) extends AnyVal
case class Odd(i: Int) extends AnyVal

class IntProps extends Specification with ScalaCheck {


  implicit val arbEven: Arbitrary[Even] = Arbitrary(Gen.posNum[Int]
    .filter(x => x % 2 == 0).map(Even))
  implicit val arbOdd: Arbitrary[Odd] = Arbitrary(Gen.posNum[Int]
    .filter(x => x % 2 != 0).map(Odd))

  "odds integers" should {
    "always be odd" in check {
      (odd: Odd) =>
        (odd.i % 2 != 0) must beTrue
    }
    "produce an even number when added together" in check {
      (odd1: Odd, odd2: Odd) =>
        val result = odd1.i + odd2.i
        (result % 2 == 0) must beTrue
    }
  }

  "even integers" should {
    "always be even" in check {
      (even: Even) =>
        (even.i % 2 == 0) must beTrue
    }
    "produce an even number when added together" in check {
      (even1: Even, even2: Even) =>
        val result = even1.i + even2.i
        (result % 2 == 0) must beTrue
    }
  }

  "an odd and an even number added together" should {
    "be odd" in check {
      (even: Even, odd: Odd) =>
        val result = even.i + odd.i
        (result % 2 != 0) must beTrue
    }
  }

  "any number added to zero" should {
    "equal the original number" in check {
      (even: Even, odd: Odd) =>
        val evenPlusZero = even.i + 0
        val oddPlusZero = odd.i + 0

        evenPlusZero mustEqual even.i
        oddPlusZero mustEqual odd.i
    }
  }


}
