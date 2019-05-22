package tkom

import kotlin.math.pow

class ComplexNumber(var realPart: Double = 0.0, var imaginaryPart: Double = 0.0): Comparable<ComplexNumber> {

  constructor(complexNumber: ComplexNumber): this(complexNumber.realPart, complexNumber.imaginaryPart)

  override fun compareTo(other: ComplexNumber): Int {
    return when {
      realPart > other.realPart -> 1
      realPart == other.realPart -> 0
      else -> -1
    }
  }

  companion object {
    val TRUE = ComplexNumber(1.0, 0.0)
    val FALSE = ComplexNumber(0.0, 0.0)
    val ZERO = ComplexNumber(0.0, 0.0)
  }

  fun add(complexNumber: ComplexNumber): ComplexNumber {
    realPart += complexNumber.realPart
    imaginaryPart += complexNumber.imaginaryPart
    return this
  }

  fun subtract(complexNumber: ComplexNumber): ComplexNumber {
    realPart -= complexNumber.realPart
    imaginaryPart -= complexNumber.imaginaryPart
    return this
  }

  fun multiply(complexNumber: ComplexNumber): ComplexNumber {
    val resultComplexNumber = ComplexNumber()
    resultComplexNumber.realPart = realPart * complexNumber.realPart - imaginaryPart * complexNumber.imaginaryPart
    resultComplexNumber.imaginaryPart = imaginaryPart * complexNumber.realPart + realPart * complexNumber.imaginaryPart
    return resultComplexNumber
  }

  fun divide(complexNumber: ComplexNumber): ComplexNumber {
    val numerator = this.multiply(complexNumber.conjugate())
    val denominator = complexNumber.multiply(complexNumber.conjugate())
    this.realPart = numerator.realPart / denominator.realPart
    this.imaginaryPart = numerator.imaginaryPart / denominator.realPart
    return this
  }

  fun modulo(complexNumber: ComplexNumber): ComplexNumber {
    return ComplexNumber(realPart % complexNumber.realPart, 0.0)
  }

  fun power(complexNumber: ComplexNumber): ComplexNumber {
    return ComplexNumber(this.realPart.pow(complexNumber.realPart))
  }

  fun and(complexNumber: ComplexNumber): ComplexNumber {
    return if (realPart > 0.0 && complexNumber.realPart > 0.0) {
      TRUE
    } else {
      FALSE
    }
  }

  fun or(complexNumber: ComplexNumber): ComplexNumber {
    return if (realPart > 0.0 || complexNumber.realPart > 0.0) {
      TRUE
    } else {
      FALSE
    }
  }

  fun negate(): ComplexNumber {
    return if (realPart > 0.0) {
      FALSE
    } else {
      TRUE
    }
  }

  fun isTrue(): Boolean {
    return realPart > 0.0
  }

  fun isFalse(): Boolean {
    return realPart <= 0.0
  }

  fun conjugate(): ComplexNumber {
    return ComplexNumber(realPart, -imaginaryPart)
  }

  override fun toString(): String {
    return when {
      imaginaryPart == 0.0 -> realPart.toString()
      realPart == 0.0 -> imaginaryPart.toString() + "i"
      imaginaryPart == 0.0 && realPart == 0.0 -> "0.0"
      else -> {
        if (imaginaryPart < 0) {
          "$realPart - ${-imaginaryPart}i"
        } else {
          "$realPart + ${imaginaryPart}i"
        }
      }
    }
  }

  override fun equals(other: Any?): Boolean {
    other as ComplexNumber
    return other.imaginaryPart == imaginaryPart && other.realPart == realPart
  }
}