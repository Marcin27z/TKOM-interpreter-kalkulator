package tkom.parser

import tkom.Token
import tkom.TokenType
import tkom.ast.ASTNode
import tkom.lexer.Lexer
import tkom.source.Source

class Parser(private val lexer: Lexer, private val source: Source) {

  lateinit var token: Token
  private var moveFlag = true
  private var errors = 0
  private var exitFlag = false
  private val errorList = arrayListOf<ParseError>()

  companion object {

    private var level = 0

    @JvmStatic
    fun printLevel() {
//      repeat(level) {
//        print(".")
//      }
    }
  }

  fun accept(symbol: TokenType): Boolean {
    advance()
    if (token.tokenType == symbol) {
      when {
        token.tokenType == TokenType.OPEN_BRACE -> level++
        token.tokenType == TokenType.CLOSE_BRACE -> level--
        //      println(symbol)
        token.tokenType == TokenType.LINE_BREAK -> printLevel()
      }
      if (token.tokenType == TokenType.EOT) {
        println("eot")
        exitFlag = true
        advance()
      }
      if (!exitFlag)
        move()
      return true
    }
    return false
  }

  fun expect(symbol: TokenType): Boolean {
    if (accept(symbol)) {
      return true
    }
    error(token, symbol)
    return false
  }

  fun advance() {
    if (moveFlag) {
      token = lexer.getToken()
      moveFlag = false
    }
  }

  fun move() {
    moveFlag = true
  }

  fun parse(): Pair<Int, Boolean> {
    print(">")
    operation()
    for (error in errorList) {
      error.printError(source.getRawInput())
    }
    source.reset()
    errorList.clear()
    return Pair(errors, exitFlag)
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
          error(token, TokenType.SEMICOLON)
        }
      }
      accept(TokenType.BREAK_KEYWORD) -> breakk()
      accept(TokenType.CONTINUE_KEYWORD) -> continuee()
      accept(TokenType.LINE_BREAK) || accept(TokenType.SEMICOLON) -> {}
      else -> {
        complexExpression()
        if (!accept(TokenType.LINE_BREAK) && !accept(TokenType.SEMICOLON)) {
          error(token, TokenType.SEMICOLON)
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

  fun callArguments() {
    val callArgumensNode = ASTNode(Token(), listOf())
    val simpleExpressionsNodes = arrayListOf<ASTNode>()
    simpleExpressionsNodes.add(simpleExpression())
    while (accept(TokenType.COMMA)) {
      simpleExpressionsNodes.add(simpleExpression())
    }
    callArgumensNode.nodes = simpleExpressionsNodes
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
        while (accept(TokenType.CLOSE_PARENTHESIS)) {
          parenthesis--
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

  fun value(): ASTNode {
    var valueNode: ASTNode = ASTNode(Token(), listOf())
    when {
      accept(TokenType.NUMBER) -> {
        val numberNode = ASTNode(token, listOf())
        if (accept(TokenType.ADDITIVE_OPERATOR)) {
          val additiveOperatorNode = ASTNode(token, listOf())
          expect(TokenType.NUMBER)
          val secondNumberNode = ASTNode(token, listOf())
          additiveOperatorNode.nodes = listOf(numberNode, secondNumberNode)
          valueNode = additiveOperatorNode
        } else {
          valueNode = numberNode
        }
      }
      accept(TokenType.IDENTIFIER) -> {
        val identifierNode = ASTNode(token, listOf())
        if (accept(TokenType.OPEN_PARENTHESIS)) {
          val argumentsNode = callArguments()
          expect(TokenType.CLOSE_PARENTHESIS)
          valueNode = ASTNode(Token(), listOf(identifierNode, argumentsNode))
        } else {
          valueNode = identifierNode
        }
      }
      accept(TokenType.NEGATION) -> {
        val negationNode = ASTNode(token, listOf())
        val negatedValueNode = value()
        negationNode.nodes = listOf(negatedValueNode)
      }
      accept(TokenType.LINE_BREAK) -> {

      }
      accept(TokenType.EOT) -> {

      }
//      else -> {
//        println("unexpected token")
//        move()
//      }
    }
    return valueNode
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
    expect(TokenType.BREAK_KEYWORD)
  }

  fun continuee() {
    expect(TokenType.CONTINUE_KEYWORD)
  }

  fun error(token: Token, tokenType: TokenType) {
    errors++
    errorList.add(ParseError(token, tokenType))
  }
}

class ParseError(
    private val token: Token,
    private val tokenType: TokenType
) {

  fun printError(sourceCharacters: String) {
    val line = token.position.line
    val sourceLine = sourceCharacters.split("\n")[line]
    println(sourceLine)
    repeat(token.position.column - 1) {
      print(" ")
    }
    println("^")
    println("line: ${token.position.line}, column: ${token.position.column} expectd $tokenType")
  }
}