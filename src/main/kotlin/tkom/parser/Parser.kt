package tkom.parser

import tkom.Token
import tkom.TokenType
import tkom.lexer.Lexer
import tkom.source.Source

class Parser(private val lexer: Lexer, private val source: Source) {

  lateinit var token: Token
  private var errors = 0

  companion object {

    private var level = 0

    @JvmStatic
    fun printLevel() {
      repeat(level) {
        print(".")
      }
    }
  }

  fun accept(symbol: TokenType): Boolean {
    if (token.tokenType == symbol) {
      if (token.tokenType == TokenType.OPEN_BRACE)
        level++
      else if (token.tokenType == TokenType.CLOSE_BRACE)
        level--
//      println(symbol)
      if (token.tokenType == TokenType.LINE_BREAK) {
        printLevel()
      }
      advance()
      return true
    }
    return false
  }

  fun expect(symbol: TokenType): Boolean {
    if (accept(symbol)) {
      return true
    }
    error(symbol)
    return false
  }

  fun advance() {
    token = lexer.getToken()
  }

  fun parse(): Int {
    advance()
    operation()
    return errors
  }

  fun operation() {
//    println("operation")
    when {
      accept(TokenType.FUN_KEYWORD) -> functionDefinition()
      accept(TokenType.LINE_BREAK) -> operation()
      else -> instruction()
    }
  }

  fun instruction() {
//    println("instruction")
    when {
      accept(TokenType.FOR_KEYWORD) -> loop()
      accept(TokenType.IF_KEYWORD) -> iff()
      accept(TokenType.RETURN_KEYWORD) -> {
        returnn()
        if (!accept(TokenType.LINE_BREAK) && !accept(TokenType.SEMICOLON)) {
          error(TokenType.SEMICOLON)
        }
      }
      accept(TokenType.BREAK_KEYWORD) -> breakk()
      accept(TokenType.CONTINUE_KEYWORD) -> continuee()
      else -> {
        complexExpression()
        if (!accept(TokenType.LINE_BREAK) && !accept(TokenType.SEMICOLON)) {
          error(TokenType.SEMICOLON)
        }
      }
    }
  }

  fun functionDefinition() {
//    println("function definition")
    expect(TokenType.IDENTIFIER)
    expect(TokenType.OPEN_PARENTHESIS)
    arguments()
    expect(TokenType.CLOSE_PARENTHESIS)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    while (!accept(TokenType.CLOSE_BRACE)) {
      instruction()
    }
    expect(TokenType.LINE_BREAK)
  }

  fun arguments() {
//    println("arguments")
    if (accept(TokenType.IDENTIFIER)) {
      while (accept(TokenType.COMMA)) {
        expect(TokenType.IDENTIFIER)
      }
    }
  }

  fun loop() {
//    println("loop")
    expect(TokenType.OPEN_PARENTHESIS)
    assignment()
    expect(TokenType.SEMICOLON)
    complexExpression()
    expect(TokenType.SEMICOLON)
    assignment()
    expect(TokenType.CLOSE_PARENTHESIS)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    while (!accept(TokenType.CLOSE_BRACE)) {
      instruction()
    }
    expect(TokenType.LINE_BREAK)
  }

  fun assignment() {
//    println("assignment")
    expect(TokenType.IDENTIFIER)
    expect(TokenType.ASSIGNMENT)
    complexExpression()
  }

  fun complexExpression() {
//    println("complex expression")
    simpleExpression()
    while (accept(TokenType.ADDITIVE_OPERATOR) || accept(TokenType.MULTIPLICATIVE_OPERATOR) || accept(TokenType.ASSIGNMENT)) {
      simpleExpression()
    }
  }

  fun simpleExpression() {
//    println("simple expression")
    when {
      accept(TokenType.NEGATION) -> {
        complexExpression()
      }
      accept(TokenType.OPEN_PARENTHESIS) -> {
        var parenthesis = 1
        while (accept(TokenType.OPEN_PARENTHESIS)) {
          parenthesis++
        }
        complexExpression()
        repeat(parenthesis) {
          expect(TokenType.CLOSE_PARENTHESIS)
        }
      }
      else -> {
        value()
        while (accept(TokenType.ADDITIVE_OPERATOR) || accept(TokenType.MULTIPLICATIVE_OPERATOR) || accept(TokenType.RELATIONAL_OPERATOR)) {
          value()
        }
      }
    }
  }

  fun value() {
//    println("value")
    when {
      accept(TokenType.NUMBER) -> {
        while (accept(TokenType.ADDITIVE_OPERATOR)) {
          expect(TokenType.NUMBER)
        }
      }
      accept(TokenType.IDENTIFIER) -> {
        if (accept(TokenType.OPEN_PARENTHESIS)) {
          arguments()
          expect(TokenType.CLOSE_PARENTHESIS)
        } else {

        }
      }
      accept(TokenType.NEGATION) -> {
        value()
      }
      accept(TokenType.LINE_BREAK) -> {

      }
      else -> {
        println("expected value")
        advance()
      }
    }
  }

  fun returnn() {
//    println("return")
    if (!accept(TokenType.CLOSE_BRACE)) {
      complexExpression()
    }
  }

  fun iff() {
//    println("if")
    expect(TokenType.OPEN_PARENTHESIS)
    complexExpression()
    expect(TokenType.CLOSE_PARENTHESIS)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    while (!accept(TokenType.CLOSE_BRACE)) {
      instruction()
    }
    expect(TokenType.LINE_BREAK)
    if (accept(TokenType.ELSE_KEYWORD)) {
      if (accept(TokenType.IF_KEYWORD)) {
        iff()
      } else {
        expect(TokenType.OPEN_BRACE)
        expect(TokenType.LINE_BREAK)
        while (!accept(TokenType.CLOSE_BRACE)) {
          instruction()
        }
        expect(TokenType.LINE_BREAK)
      }
    }
  }

  fun breakk() {

  }

  fun continuee() {

  }

  fun error(tokenType: TokenType) {
    println("expectd $tokenType")
    errors++
  }
}