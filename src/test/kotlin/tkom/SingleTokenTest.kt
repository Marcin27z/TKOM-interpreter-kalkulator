package tkom

import org.junit.Test
import tkom.lexer.Lexer
import tkom.source.CommandLineSource
import kotlin.test.assertEquals

class SingleTokenTest {

  @Test
  fun returnedNegationWhenExclamationMarkEncountered() {
    val source = CommandLineSource("!".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "!", tokenType = TokenType.NEGATION, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedOpenParenthesisWhenOpenParenthesisEncountered() {
    val source = CommandLineSource("(".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "(", tokenType = TokenType.OPEN_PARENTHESIS, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedCloseParenthesisWhenCloseParenthesisEncountered() {
    val source = CommandLineSource(")".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = ")", tokenType = TokenType.CLOSE_PARENTHESIS, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedOpenBracesWhenOpenBracesEncountered() {
    val source = CommandLineSource("{".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "{", tokenType = TokenType.OPEN_BRACE, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedCloseBracesWhenCloseBracesEncountered() {
    val source = CommandLineSource("}".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "}", tokenType = TokenType.CLOSE_BRACE, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedAdditiveOperatorWhenPlusSignEncountered() {
    val source = CommandLineSource("+".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "+", tokenType = TokenType.ADDITIVE_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedAdditiveOperatorWhenMinusSignEncountered() {
    val source = CommandLineSource("-".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "-", tokenType = TokenType.ADDITIVE_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedAssignmentWhenEqualSignEncountered() {
    val source = CommandLineSource("=".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "=", tokenType = TokenType.ASSIGNMENT, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedComparisonOperatorWhenTwoEqualSignsEncountered() {
    val source = CommandLineSource("==".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "==", tokenType = TokenType.RELATIONAL_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedComparisonOperatorWhenGreaterSignsEncountered() {
    val source = CommandLineSource(">".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = ">", tokenType = TokenType.RELATIONAL_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedComparisonOperatorWhenLessSignsEncountered() {
    val source = CommandLineSource("<".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "<", tokenType = TokenType.RELATIONAL_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedComparisonOperatorWhenNegationAndEqualSignsEncountered() {
    val source = CommandLineSource("!=".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "!=", tokenType = TokenType.RELATIONAL_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedComparisonOperatorWhenGreaterAndEqualSignsEncountered() {
    val source = CommandLineSource(">=".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = ">=", tokenType = TokenType.RELATIONAL_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedComparisonOperatorWhenLessAndEqualSignsEncountered() {
    val source = CommandLineSource("<=".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "<=", tokenType = TokenType.RELATIONAL_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedKeywordWhenForKeyWordEncountered() {
    val anyKeyWord = "for"
    val source = CommandLineSource(anyKeyWord.byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = anyKeyWord, tokenType = TokenType.FOR_KEYWORD, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedIdentifierWhenAnyWordDifferentThanKeyWordEncountered() {
    val anyKeyWord = "asdgsdfgsdfg"
    val source = CommandLineSource(anyKeyWord.byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = anyKeyWord, tokenType = TokenType.IDENTIFIER, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedMultiplicativeOperatorWhenMultiplicationSignEncountered() {
    val source = CommandLineSource("*".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "*", tokenType = TokenType.MULTIPLICATIVE_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedMultiplicativeOperatorWhenDivisionSignEncountered() {
    val source = CommandLineSource("/".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "/", tokenType = TokenType.MULTIPLICATIVE_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedMultiplicativeOperatorWhenPercentSignEncountered() {
    val source = CommandLineSource("%".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "%", tokenType = TokenType.MULTIPLICATIVE_OPERATOR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedMultiplicativeOperatorWhenTwoAmpersandsEncountered() {
    val source = CommandLineSource("&&".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "&&", tokenType = TokenType.LOGICAL_AND, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedMultiplicativeOperatorWhenTwoPipesEncountered() {
    val source = CommandLineSource("||".byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = "||", tokenType = TokenType.LOGICAL_OR, position = Position(0, 1,1)), token)
  }

  @Test
  fun returnedNumberAnyNumberEncountered() {
    val anyNumber = "1908237.87243i"
    val source = CommandLineSource(anyNumber.byteInputStream())
    val token = Lexer(source).getToken()
    assertEquals(Token(value = anyNumber, tokenType = TokenType.NUMBER, position = Position(0, 1,1))
        .apply { complexNumber = ComplexNumber(0.0, 1908237.87243) }, token)
  }
}