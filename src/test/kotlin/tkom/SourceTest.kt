package tkom

import org.junit.Test
import tkom.source.CommandLineSource
import kotlin.test.assertEquals

class SourceTest {

  @Test
  fun gotExpectedCharacter() {
    val source = CommandLineSource("a".byteInputStream())
    val character = source.getNextChar()
    assertEquals(Character().apply { char = 'a'; position = Position(0, 1, 1) }, character)
  }
}