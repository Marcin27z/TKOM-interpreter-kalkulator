package tkom

class ComplexNumber(var realPart: Double = 0.0, var imaginaryPart: Double = 0.0) {

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
    realPart = realPart * complexNumber.realPart - imaginaryPart * complexNumber.imaginaryPart
    imaginaryPart = imaginaryPart * complexNumber.realPart + realPart * complexNumber.imaginaryPart
    return this
  }

  override fun equals(other: Any?): Boolean {
    other as ComplexNumber
    return other.imaginaryPart == imaginaryPart && other.realPart == realPart
  }
}