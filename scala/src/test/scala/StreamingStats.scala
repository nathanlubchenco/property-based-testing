import org.specs2.mutable._
import org.specs2.ScalaCheck
import org.scalacheck._

class StreamingStats extends Specification with ScalaCheck {

  def streamingMean(data: List[Double]): Double = {
    def go(data: List[Double], mean: Double, count: Int): Double = {
      if (data.isEmpty) mean
      else {
        val newMean = ((mean * count) + data.head) / (count + 1)
        go(data.tail, newMean, count +1)
      }
    }
    go(data, 0, 0)
  }

  sealed trait StatsError
  case class InsufficientData(s: String) extends StatsError

  def streamingMean2(data: List[Double]) : Either[StatsError, Double] = {
    def go(data: List[Double], mean: Double, count: Int): Double = {
      if (data.isEmpty) mean
      else {
        val newMean = ((mean * count) + data.head) / (count + 1)
        go(data.tail, newMean, count +1)
      }
    }
    if(data.isEmpty) Left(InsufficientData(s"Could not compute mean for $data"))
    else Right(go(data, 0, 0))
  }

  implicit val arbDoubleList: Arbitrary[List[Double]] = Arbitrary(Gen.listOf[Double](Gen.chooseNum(-1000, 1000)))

  "streamingMean" should {
    "produce the same mean as a non-streaming mean" in check {
      (doubles: List[Double]) =>
        val streamingResult = streamingMean(doubles)
        val checkResult = doubles.sum / doubles.size

        streamingResult must beCloseTo(checkResult, 0.001)
    }
  }

  "streamingMean2" should {
    "produce the same mean as a non-streaming mean OR an error" in check {
      (skipped)
      (doubles: List[Double]) =>
        val streamingResult = streamingMean2(doubles)
        val checkResult = doubles.sum / doubles.size

        streamingResult.fold(err => err mustEqual(InsufficientData(s"Could not compute mean for List()")), suc => suc must beCloseTo(checkResult, 0.001))
    }
  }
}
