package tkom

class Character {
  var char = 0.toChar()
  var position = Position(0, 0, 0)

  fun isSpace() = char == ' '

  fun isLineSeparator() = char == '\n' || char == '\r'

  fun isHex() = char in 'a'..'f' || char in 'A'..'F' || char.isDigit()

  fun isBinary() = char in '0'..'1'

  fun isOct() = char in '0'..'7'

  fun toNumber() = (char - '0').toDouble()

  fun updatePosition() {
    if (char == '\n') {
      position.nextLine()
    } else {
      position.nextColumn()
      position.nextIndex()
    }
  }

  fun reset() {
    position.reset()
  }

  override fun equals(other: Any?): Boolean {
    other as Character
    return char == other.char && position == other.position
  }
}