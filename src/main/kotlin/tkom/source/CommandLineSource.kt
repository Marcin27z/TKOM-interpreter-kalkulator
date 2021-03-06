package tkom.source

import tkom.Character
import java.io.InputStream
import java.io.InputStreamReader

class CommandLineSource(stream: InputStream) : Source {


  private var reader = InputStreamReader(stream)

  private var currentCharacter = Character()
  private var move = true
  private val rawInput = StringBuilder()

  override fun setInputStream(stream: InputStream) {
    reader = InputStreamReader(stream)
  }

  override fun moveToNext() {
    move = true
  }

  override fun getChar(): Character {
    if (move) {
      val read = reader.read()
      if (read == -1) {
        currentCharacter.char = '\u0000'
      } else {
        currentCharacter.char = read.toChar()
        rawInput.append(currentCharacter.char)
      }
      currentCharacter.updatePosition()
      move = false
    }
    return currentCharacter
  }

  override fun getNextChar(): Character {
    moveToNext()
    return getChar()
  }

  override fun reset() {
    rawInput.setLength(0)
    currentCharacter.reset()
  }

  override fun getRawInput(): String {
    return rawInput.toString()
  }
}