package tkom.source

import tkom.Character
import java.io.InputStream
import java.io.InputStreamReader

class CommandLineSource(stream: InputStream) : Source {

  private var reader = InputStreamReader(stream)

  private var currentCharacter = Character()
  private var move = true

  override fun setInputStream(stream: InputStream) {
    reader = InputStreamReader(stream)
  }

  override fun moveToNext() {
    move = true
  }

  override fun getChar(): Character {
    if (move) {
      currentCharacter.char = reader.read().toChar()
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
    currentCharacter.reset()
  }
}