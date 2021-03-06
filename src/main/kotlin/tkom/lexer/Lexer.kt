package tkom.lexer

import tkom.Position
import tkom.Token
import tkom.TokenType
import tkom.source.Source
import java.math.BigDecimal
import java.math.RoundingMode

class Lexer(private val source: Source) {

  private val charMap = HashMap<Char, (Position) -> Token>()
  private val keywordSet = HashSet<String>()
  private val builtInFunctionsSet = HashSet<String>()
  private val specialWordsSet = HashSet<String>()

  init {
    initMap()
    initKeyWords()
    initSpecialWords()
    initBuiltInFunctions()
  }

  private fun initKeyWords() {
    keywordSet.addAll(listOf("for", "if", "else", "return", "break", "continue", "fun"))
  }

  private fun initBuiltInFunctions() {
    builtInFunctionsSet.addAll(listOf("print", "println", "sqrt"))
  }

  private fun initSpecialWords() {
    specialWordsSet.addAll(listOf("exit"))
  }

  private fun initMap() {
    (48..57).forEach { d ->
      // 0..9
      charMap[d.toChar()] = { gotDigit(it) }
    }
    ('a'..'z').forEach { c ->
      charMap[c] = { gotLetter(it) }
      charMap[c.toUpperCase()] = { gotLetter(it) }
    }
    charMap['='] = { gotEqualSign(it) }
    charMap['+'] = { gotPlusSign(it) }
    charMap['-'] = { gotMinusSign(it) }
    charMap['*'] = { gotMultiplicationSign(it) }
    charMap['/'] = { gotDivisionSign(it) }
    charMap['^'] = { gotCircumflexAccent(it) }
    charMap['('] = { gotLeftParenthesis(it) }
    charMap['{'] = { gotLeftBrace(it) }
    charMap[')'] = { gotRightParenthesis(it) }
    charMap['}'] = { gotRightBrace(it) }
    charMap['%'] = { gotPercentSign(it) }
    charMap['!'] = { gotExclamationMark(it) }
    charMap['>'] = { gotGreaterSign(it) }
    charMap['<'] = { gotLessSign(it) }
    charMap['^'] = { gotCircumflexAccent(it) }
    charMap['&'] = { gotAmpersand(it) }
    charMap['|'] = { gotPipe(it) }
    charMap[','] = { gotComma(it) }
    charMap[';'] = { gotSemicolon(it) }
    charMap['\u0000'] = { gotEOT(it) }
  }

  @Throws(InvalidCharacterException::class)
  fun getToken(): Token {
    var ch = source.getChar()
    while (ch.isSpace()) {
      ch = source.getNextChar()
    }
    if (ch.isLineSeparator()) {
      val position = Position(ch.position)
      source.moveToNext()
      return Token(position = position, value = "\n", tokenType = TokenType.LINE_BREAK)
    }
    try {
      return charMap[ch.char]!!.invoke(ch.position)
    } catch (e: InvalidCharacterException) {
      println("invalid character")
    } catch (e: NullPointerException) {
      println("invalid character")
      source.moveToNext()
    }
    return Token()
  }

  @Throws(InvalidCharacterException::class)
  private fun gotDigit(start: Position): Token { // 0..9

    val position = Position(start)
    val function: (tkom.Character) -> Boolean
    var divider: Double
    val multiplier: Double
    var char = source.getChar()
    var number = char.toNumber()
    val token = Token(position = position, tokenType = TokenType.NUMBER)
    val stringBuilder = StringBuilder().append(char.char)
    var skip = false

    when (char.char) {
      '0' -> when (source.getNextChar().char) {
        'x' -> {
          stringBuilder.append('x')
          function = { character -> character.isHex() }
          divider = 16.0
          multiplier = 16.0
        }
        'b' -> {
          stringBuilder.append('b')
          function = { character -> character.isBinary() }
          divider = 2.0
          multiplier = 2.0
        }
        in '0'..'7' -> {
          function = { character -> character.isOct() }
          divider = 8.0
          multiplier = 8.0
        }
        '.' -> {
          function = { character: tkom.Character -> character.char.isDigit() }
          divider = 10.0
          multiplier = 10.0
          skip = true
        }
        else -> {
          return token.apply { value = "0" }
        }
      }
      else -> {
        function = { character: tkom.Character -> character.char.isDigit() }
        divider = 10.0
        multiplier = 10.0
      }

    }

    if (!skip) {
      char = source.getNextChar()
      while (function(char)) {
        stringBuilder.append(char.char)
        number = number * multiplier + char.toNumber()
        char = source.getNextChar()
      }
    }
    if (char.char == '.' || skip) {
      char = source.getNextChar()
      stringBuilder.append('.')
      if (!function(char)) throw InvalidCharacterException()
      val div = divider
      var i = 0
      while (function(char)) {
        stringBuilder.append(char.char)
        i++
        number += char.toNumber() / divider
        divider *= div
        char = source.getNextChar()
      }
      number = BigDecimal(number).setScale(i, RoundingMode.HALF_UP).toDouble()
    }
    if (source.getChar().char == 'i') {
      stringBuilder.append('i')
      token.apply { value = stringBuilder.toString(); complexNumber.imaginaryPart = number }
      source.moveToNext()
    } else {
      token.apply { value = stringBuilder.toString(); complexNumber.realPart = number }
    }
    return token
  }

  private fun gotLetter(start: Position): Token { // litery
    val position = Position(start)
    val stringBuilder = StringBuilder().append(source.getChar().char)
    var char = source.getNextChar()
    while (char.char.isLetterOrDigit()) {
      stringBuilder.append(char.char)
      char = source.getNextChar()
    }
    val builtString = stringBuilder.toString()
    return when {
      keywordSet.contains(builtString) -> {
        val token = when (builtString) {
          "for" -> Token(position = position, value = builtString, tokenType = TokenType.FOR_KEYWORD)
          "if" -> Token(position = position, value = builtString, tokenType = TokenType.IF_KEYWORD)
          "else" -> Token(position = position, value = builtString, tokenType = TokenType.ELSE_KEYWORD)
          "return" -> Token(position = position, value = builtString, tokenType = TokenType.RETURN_KEYWORD)
          "fun" -> Token(position = position, value = builtString, tokenType = TokenType.FUN_KEYWORD)
          "break" -> Token(position = position, value = builtString, tokenType = TokenType.BREAK_KEYWORD)
          "continue" -> Token(position = position, value = builtString, tokenType = TokenType.CONTINUE_KEYWORD)
          else -> Token(position = position, value = builtString, tokenType = TokenType.IDENTIFIER)
        }
        token
      }
      builtInFunctionsSet.contains(builtString) -> Token(position = position, value = builtString, tokenType = TokenType.BUILT_IN_FUNCTION)
      specialWordsSet.contains(builtString) -> Token(position = position, value = builtString, tokenType = TokenType.EOT)
      else -> Token(position = position, value = builtString, tokenType = TokenType.IDENTIFIER)
    }
  }

  private fun gotEqualSign(start: Position): Token { // =
    val position = Position(start)
    val nextChar = source.getNextChar().char
    return if (nextChar != '=') {
      Token(position = position, value = "=", tokenType = TokenType.ASSIGNMENT)
    } else {
      source.moveToNext()
      Token(position = position, value = "==", tokenType = TokenType.RELATIONAL_OPERATOR)
    }
  }

  private fun gotPlusSign(start: Position): Token { // +
    val position = Position(start)
    source.moveToNext()
    return Token(position = position, value = "+", tokenType = TokenType.ADDITIVE_OPERATOR)
  }

  private fun gotMinusSign(start: Position): Token { // -
    source.moveToNext()
    return Token(position = Position(start), value = "-", tokenType = TokenType.ADDITIVE_OPERATOR)
  }

  private fun gotCircumflexAccent(start: Position): Token { // ^
    source.moveToNext()
    return Token(position = Position(start), value = "^", tokenType = TokenType.POWER_OPERATOR)
  }

  private fun gotMultiplicationSign(start: Position): Token { // *
    source.moveToNext()
    return Token(position = Position(start), value = "*", tokenType = TokenType.MULTIPLICATIVE_OPERATOR)
  }

  private fun gotDivisionSign(start: Position): Token { // /
    source.moveToNext()
    return Token(position = Position(start), value = "/", tokenType = TokenType.MULTIPLICATIVE_OPERATOR)
  }

  private fun gotLeftParenthesis(start: Position): Token { // (
    source.moveToNext()
    return Token(position = Position(start), value = "(", tokenType = TokenType.OPEN_PARENTHESIS)
  }

  private fun gotRightParenthesis(start: Position): Token { // )
    source.moveToNext()
    return Token(position = Position(start), value = ")", tokenType = TokenType.CLOSE_PARENTHESIS)
  }

  private fun gotLeftBrace(start: Position): Token { // {
    source.moveToNext()
    return Token(position = Position(start), value = "{", tokenType = TokenType.OPEN_BRACE)
  }

  private fun gotRightBrace(start: Position): Token { // }
    source.moveToNext()
    return Token(position = Position(start), value = "}", tokenType = TokenType.CLOSE_BRACE)
  }

  private fun gotExclamationMark(start: Position): Token { // !
    val position = Position(start)
    return if (source.getNextChar().char != '=') {
      Token(position = position, value = "!", tokenType = TokenType.NEGATION)
    } else {
      source.moveToNext()
      Token(position = position, value = "!=", tokenType = TokenType.RELATIONAL_OPERATOR)
    }
  }

  private fun gotPercentSign(start: Position): Token { // %
    source.moveToNext()
    return Token(position = Position(start), value = "%", tokenType = TokenType.MULTIPLICATIVE_OPERATOR)
  }

  private fun gotLessSign(start: Position): Token { // <
    val position = Position(start)
    return if (source.getNextChar().char != '=') {
      Token(position = position, value = "<", tokenType = TokenType.RELATIONAL_OPERATOR)
    } else {
      source.moveToNext()
      Token(position = position, value = "<=", tokenType = TokenType.RELATIONAL_OPERATOR)
    }
  }

  private fun gotGreaterSign(start: Position): Token { // >
    val position = Position(start)
    return if (source.getNextChar().char != '=') {
      Token(position = position, value = ">", tokenType = TokenType.RELATIONAL_OPERATOR)
    } else {
      source.moveToNext()
      Token(position = position, value = ">=", tokenType = TokenType.RELATIONAL_OPERATOR)
    }
  }

  private fun gotAmpersand(start: Position): Token { // &
    val position = Position(start)
    return if (source.getNextChar().char != '&') {
      throw InvalidCharacterException()
    } else {
      source.moveToNext()
      Token(position = position, value = "&&", tokenType = TokenType.LOGICAL_AND)
    }
  }

  private fun gotPipe(start: Position): Token { // |
    val position = Position(start)
    return if (source.getNextChar().char != '|') {
      throw InvalidCharacterException()
    } else {
      source.moveToNext()
      Token(position = position, value = "||", tokenType = TokenType.LOGICAL_OR)
    }
  }

  private fun gotComma(start: Position): Token { // ,
    source.moveToNext()
    return Token(position = Position(start), value = ",", tokenType = TokenType.COMMA)
  }

  private fun gotSemicolon(start: Position): Token { // ;
    source.moveToNext()
    return Token(position = Position(start), value = ";", tokenType = TokenType.SEMICOLON)
  }

  private fun gotEOT(start: Position): Token {
    return Token(position = start, tokenType = TokenType.EOT)
  }
}

class InvalidCharacterException : Exception()