package tkom.source

import tkom.Character
import java.io.InputStream

interface Source {

  fun setInputStream(stream: InputStream)

  fun getChar(): Character

  fun getNextChar(): Character

  fun moveToNext()

  fun reset()
}