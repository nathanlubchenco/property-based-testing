import org.specs2.mutable._
import org.specs2.ScalaCheck
import org.scalacheck._
import org.joda.time._
import org.joda.time.format.DateTimeFormat
import scalaz.{Ordering, Order}
import scalaz.syntax.order._

class TimeClamping extends Specification with ScalaCheck {
  
  def clampInterval(interval: Interval, boundingInterval: Interval): Interval = {
    val start = interval.getStartMillis
    val end = interval.getEndMillis
    val boundingStart = boundingInterval.getStartMillis
    val boundingEnd = boundingInterval.getEndMillis

    if( start > boundingEnd | end < boundingStart) boundingInterval else {
      val clampedStart = if( start < boundingStart) boundingStart else start
      val clampedEnd = if( end > boundingEnd) boundingEnd else end

      new Interval(clampedStart, clampedEnd)
    }
  }
val UTC = DateTimeZone.UTC
val fmt = DateTimeFormat.forPattern("YYYY-MM-dd:HH-mm-ss").withZone(UTC)
def parseInstant(s: String) = fmt.parseDateTime(s).toInstant

val genInstant: Gen[Instant] = Gen.choose(parseInstant("2011-01-01:11-32-00").getMillis, parseInstant("2015-01-01:11-32-00").getMillis).map(new Instant(_))

 val readableInstantOrder: Order[ReadableInstant] = new Order[ReadableInstant] {
       def order(x: ReadableInstant, y: ReadableInstant): Ordering = Ordering.fromInt(x.compareTo(y))
         }
implicit val instantOrder: Order[Instant] = readableInstantOrder.contramap(identity)

 val genInterval: Gen[Interval] = for {
  inst1 <- genInstant
  inst2 <- genInstant
} yield {
  val start = inst1 min inst2
  val end = inst1 max inst2

  println(start)
  println(end)
  new Interval(start, end)
}
implicit val arbInterval: Arbitrary[Interval] = Arbitrary(genInterval)


  "clamping" should {
    """produce an interval with a length of 
     (A) bounding end - bounding start -
     (B) (if positive) start - boundingStart -
     (C) (if positive) end - boundingEnd""" in check {
       (skipped)
       (interval: Interval, bInterval: Interval) =>
       val clamped = clampInterval(interval, bInterval)

       val bEndMinusbStart = bInterval.getEndMillis - bInterval.getStartMillis
       val startMinusbStart = interval.getStartMillis - bInterval.getStartMillis
       val endMinusbEnd = bInterval.getEndMillis - interval.getEndMillis

       val A = bEndMinusbStart
       val B = if(startMinusbStart > 0) startMinusbStart else 0
       val C = if(endMinusbEnd > 0) endMinusbEnd else 0

println(A)
println(B)
println(C)

       val expectedLength = A - B - C
       val actualLength = clamped.getEndMillis - clamped.getStartMillis

       expectedLength mustEqual actualLength
     }
   }


}
