---
title       : Property Based Testing
subtitle    : Get more, for less
author      : Nathan Lubchenco | @nathanlubchenco
job         : Data Scientist @simpleenergy
framework   : io2012        # {io2012, html5slides, shower, dzslides, ...}
highlighter : highlight.js  # {highlight.js, prettify, highlight}
hitheme     : tomorrow      # 
widgets     : []            # {mathjax, quiz, bootstrap}
mode        : selfcontained # {standalone, draft}
---
## why should i pay attention for the next
## ~15 minutes?

### if all goes well, you'll:

* discover bugs you wouldn't have otherwise caught

* have to think less hard

* get to work less hard

---

## What is Property Based Testing?
Instead of testing that a function produces the appropriate output from a specific input, test that a property holds for a range of generated inputs
### Unit Based Example for addition
* 2 + 2 = 4
* 0 + 10 = 10
* -2 + 2 = 0

### Property Based example for addition
* odd + odd = even
* even + odd = odd
* negative + negative = negative
* n + zero = n

---

## Some Libraries

* QuickCheck (Haskell)
* ScalaCheck
* qc.js 
* rubycheck
* qc.py
* numerous others (check wikipedia for links)

---

## ScalaCheck Example: setup
    import org.specs2.mutable._
    import org.specs2.ScalaCheck
    import org.scalacheck._

    case class Even(i: Int) extends AnyVal
    case class Odd(i: Int) extends AnyVal
    
    implicit val arbEven: Arbitrary[Even] = Arbitrary(Gen.posNum[Int]
      .filter(x => x % 2 == 0).map(Even))
    implicit val arbOdd: Arbitrary[Odd] = Arbitrary(Gen.posNum[Int]
      .filter(x => x % 2 != 0).map(Odd))

    class IntProps extends Specification with ScalaCheck {
    ....
    }

---

## ScalaCheck Example 1:
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
    

---

## ScalaCheck Example 2:
    
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

---


## ScalaCheck Example 3:

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

---

## Ok, so what? Or, how can this really help me?

* Working with Collections such as Lists
  * generation of empty collections
  
* Working with numbers
  * generation of negatives, zero, large numbers
  
* Working with Dates / Times
  * generation of values crossing over boundaries such as years or months
  
* Testing serialization
  * checking round trips for many possible values very quickly

---

## Common Pitfalls

* Re-implementing a function
* Can be difficult to determine what properties something should have
   * Forces you to think carefully about what a function is doing
* Some generators are complex
  * but, not everything is a nail


---

## Less Trivial Examples

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

---

## This Test Reveals a Problem

    "streamingMean" should {
        "produce the same mean as a non-streaming mean" in check {
          (doubles: List[Double]) =>
            val streamingResult = streamingMean(doubles)
            val checkResult = doubles.sum / doubles.size
    
            streamingResult must beCloseTo(checkResult, 0.001)
        }
      }

---

## A Possible Solution

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

---

## A Final Example

### Interval Clamping
### 1:
#### |-----------|  

#### ----|-----------|  

#### ----X-------X  
### 2:
#### -|-------|
#### |--------------|
#### -X-------X

---

### 3:
#### |---------------|
#### ----|-------|
#### ----X-------X
    
### 4:    
#### |-------|
#### ---|--------|
#### ---X----X

---
### 5:
#### |------|
#### -----------|------|
####  ----???----
       
### 6:
####  ------------|----------|
#### -|-------|---------
####  -------???------

---

## Summary

### Property-Based Testing
* allows you to generate hundreds of cases instead of manually generating a few
* effectively reveals edge cases you haven't considered
  * Every few weeks on Twitter someone mentions a bug discovered after running property based tests thousands of times
* helps sharpen your understanding of what exactly a function is supposed to be doing
  * writing properties can be difficult, but they reveal the underlying structure of a function
* in many cases actually takes less time to write

**Check out a library in a language you work in!**


**Thanks** 

Slides and code at: 
https://github.com/nathanlubchenco/property-based-testing
