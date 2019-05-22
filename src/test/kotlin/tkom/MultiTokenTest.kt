package tkom

import org.junit.Test
import tkom.lexer.Lexer
import tkom.source.CommandLineSource
import kotlin.test.assertEquals

class MultiTokenTest {

  @Test
  fun test() {
    val source = CommandLineSource("0.5 + 0.5i\u0000".byteInputStream())
    val tokenList = ArrayList<Token>(3)
    val lexer = Lexer(source)
    do {
      val token = lexer.getToken()
      tokenList.add(token)
    } while (token.tokenType != TokenType.EOT)
    assertEquals(Token(value = "0.5", tokenType = TokenType.NUMBER, position = Position(0, 1,1))
        .apply { complexNumber = ComplexNumber(0.5, 0.0) }, tokenList[0])
    assertEquals(Token(value = "0.5i", tokenType = TokenType.NUMBER, position = Position(0, 7,7))
        .apply { complexNumber = ComplexNumber(0.0, 0.5) }, tokenList[2])
    assertEquals(Token(value = "+", tokenType = TokenType.ADDITIVE_OPERATOR, position = Position(0, 5,5)), tokenList[1])
  }

  @Test
  fun test2() {
    val source = CommandLineSource("a == a + 5\u0000".byteInputStream())
    val lexer = Lexer(source)
    do {
      val token = lexer.getToken()
      println(token)
    } while (token.tokenType != TokenType.EOT)
  }

  @Test
  fun test3() {
    val source = CommandLineSource("5 && 5\u0000".byteInputStream())
    val lexer = Lexer(source)
    do {
      val token = lexer.getToken()
      println(token)
    } while (token.tokenType != TokenType.EOT)
  }
}