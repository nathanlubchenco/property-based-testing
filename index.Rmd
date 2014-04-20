---
title       : Property Based Testing
subtitle    : Get more, for less
author      : Nathan Lubchenco
job         : Data Scientist @simpleenergy
framework   : io2012        # {io2012, html5slides, shower, dzslides, ...}
highlighter : highlight.js  # {highlight.js, prettify, highlight}
hitheme     : tomorrow      # 
widgets     : []            # {mathjax, quiz, bootstrap}
mode        : selfcontained # {standalone, draft}
---
## why should i pay attention for the next ~15 minutes?

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

## ScalaCheck Example -- the function

    def streamingMean(data: Stream[Double]): Error \/ Mean = {
      def go(data: Stream[Double], mean: Double, count: Double): Mean = {
        if (data.isEmpty) Mean(mean)
        else {
          val newMean = ((mean * count) + data.head) / (count + 1)
          go(data.tail, newMean, count + 1)
        }
      }
      if (data.isEmpty) EmptyStream.left
      else go(data, 0, 0).right
    }

---

## ScalaCheck Example -- helpers

    implicit val arbStream: Arbitrary[Stream[Double]] = 
      Arbitrary(Gen.containerOf[Stream, Double](Gen.posNum[Double]))
  
    def withinErrorBounds(d1: Double, d2: Double, error: Double) = 
      if (d1 > (d2 - error) && d1 < (d2 + error)) true else false

    def emptyStreamOrNoReplacement(error: Error) = 
      if (error == EmptyStream || error == NoReplacement) true else false

---

## ScalaCheck Example -- the test

    "streamingMean" should {
      "return the correct mean" in check {
        (data: Stream[Double]) =>
          val streamedMean = StreamingStats.streamingMean(data)
          if (data.isEmpty) {
            streamedMean.fold(error => EmptyStream mustEqual error, _ => ???)
          }
          else {
            val mean = (data.foldLeft(0.0)(_ + _) / data.size)
            streamedMean.fold(
              error => EmptyStream mustEqual error, 
              result => withinErrorBounds(result.d, mean, 0.001d) must beTrue
            )
          } 
      }
    }



