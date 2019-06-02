package tkom

import kotlin.math.pow

class ComplexNumber(var realPart: Double = 0.0, var imaginaryPart: Double = 0.0, private val isNone: Boolean = false) : Comparable<ComplexNumber> {

  init {
    if (realPart == Double.POSITIVE_INFINITY || imaginaryPart == Double.POSITIVE_INFINITY) {
      throw OverflowException("Overflow occured")
    }
    if (realPart == Double.NEGATIVE_INFINITY || imaginaryPart == Double.NEGATIVE_INFINITY) {
      throw OverflowException("Underflow occured")
    }
  }

  constructor(complexNumber: ComplexNumber) : this(complexNumber.realPart, complexNumber.imaginaryPart)

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
    val NONE = ComplexNumber(0.0, 0.0, true)
  }

  fun add(complexNumber: ComplexNumber): ComplexNumber {
    if (this.isNone) {
      return NONE
    }
    return ComplexNumber(realPart + complexNumber.realPart, imaginaryPart + complexNumber.imaginaryPart)
  }

  fun subtract(complexNumber: ComplexNumber): ComplexNumber {
    if (this.isNone) {
      return NONE
    }
    return ComplexNumber(realPart - complexNumber.realPart, imaginaryPart - complexNumber.imaginaryPart)
  }

  fun multiply(complexNumber: ComplexNumber): ComplexNumber {
    if (this.isNone) {
      return NONE
    }
    if (complexNumber.isNone) {
      return ComplexNumber(realPart, imaginaryPart)
    }
    return ComplexNumber(realPart * complexNumber.realPart - imaginaryPart * complexNumber.imaginaryPart,
        imaginaryPart * complexNumber.realPart + realPart * complexNumber.imaginaryPart)
  }

  fun divide(complexNumber: ComplexNumber): ComplexNumber {
    if (this.isNone) {
      return NONE
    }
    if (complexNumber.isNone) {
      return ComplexNumber(realPart, imaginaryPart)
    }
    if (complexNumber == ZERO) {
      throw DivideByZeroException("Division by zero occured")
    }
    val numerator = this.multiply(complexNumber.conjugate())
    val denominator = complexNumber.multiply(complexNumber.conjugate())
    return ComplexNumber(numerator.realPart / denominator.realPart, numerator.imaginaryPart / denominator.realPart)
  }

  fun modulo(complexNumber: ComplexNumber): ComplexNumber {
    if (this.isNone) {
      return NONE
    }
    if (complexNumber.isNone) {
      return ComplexNumber(realPart, imaginaryPart)
    }
    if (complexNumber == ZERO) {
      throw DivideByZeroException("Division by zero occured")
    }
    return ComplexNumber(realPart % complexNumber.realPart, 0.0)
  }

  fun power(complexNumber: ComplexNumber): ComplexNumber {
    if (this.isNone) {
      return NONE
    }
    if (complexNumber.isNone) {
      return ComplexNumber(realPart, imaginaryPart)
    }
    return ComplexNumber(this.realPart.pow(complexNumber.realPart))
  }

  fun and(complexNumber: ComplexNumber): ComplexNumber {
    if (this.isNone) {
      return NONE
    }
    if (complexNumber.isNone) {
      return TRUE
    }
    return if (realPart > 0.0 && complexNumber.realPart > 0.0) {
      TRUE
    } else {
      FALSE
    }
  }

  fun or(complexNumber: ComplexNumber): ComplexNumber {
    if (this.isNone) {
      return NONE
    }
    return if (realPart > 0.0 || complexNumber.realPart > 0.0) {
      TRUE
    } else {
      FALSE
    }
  }

  fun negate(): ComplexNumber {
    if (this.isNone) {
      return NONE
    }
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

  fun isNone(): Boolean {
    return isNone
  }

  private fun conjugate(): ComplexNumber {
    return ComplexNumber(realPart, -imaginaryPart)
  }

  fun set(complexNumber: ComplexNumber) {
    this.realPart = complexNumber.realPart
    this.imaginaryPart = complexNumber.imaginaryPart
  }

  override fun toString(): String {
    return when {
      isNone -> "None"
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

  fun opposite() {
    this.realPart = -this.realPart
    this.imaginaryPart = -this.imaginaryPart
  }

  override fun equals(other: Any?): Boolean {
    other as ComplexNumber
    return other.imaginaryPart == imaginaryPart && other.realPart == realPart && other.isNone == isNone
  }

  class DivideByZeroException(message: String) : Exception(message) {

  }

  class OverflowException(message: String) : Exception(message) {

  }
}