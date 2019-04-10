package tkom

class Token (

  var value: String = "",
  var position: Position = Position(),
  var tokenType: TokenType= TokenType.EMPTY

) {
  var complexNumber = ComplexNumber()

  override fun equals(other: Any?): Boolean {
    other as Token
    return other.value == value && other.position == position && other.tokenType == tokenType && other.complexNumber == complexNumber
  }

  override fun toString(): String {
    var numericValue = "numeric value: "
    if (complexNumber.realPart != 0.0) {
      numericValue += complexNumber.realPart.toString()
      return "$value: $tokenType: $numericValue"
    } else if (complexNumber.imaginaryPart != 0.0) {
      numericValue += complexNumber.imaginaryPart.toString() + "i"
      return "$value: $tokenType: $numericValue"
    }
    return "$value: $tokenType"
  }
}