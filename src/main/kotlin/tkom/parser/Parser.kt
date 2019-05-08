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
    val rootNode = operation()
    for (error in errorList) {
      error.printError(source.getRawInput())
    }
    source.reset()
    errorList.clear()
    return Pair(errors, exitFlag)
  }

  fun operation(): ASTNode {
    return when {
      accept(TokenType.FUN_KEYWORD) -> {
        val funKeywordNode = ASTNode(token, listOf())
        val functionDefinitionNode = functionDefinition()
        funKeywordNode.nodes = listOf(functionDefinitionNode)
        funKeywordNode
      }
      accept(TokenType.LINE_BREAK) -> {
        operation()
      }
      else -> {
        instruction()
      }
    }
  }

  fun instruction(): ASTNode {
    return when {
      accept(TokenType.FOR_KEYWORD) -> {
        loop()
      }
      accept(TokenType.IF_KEYWORD) -> {
        iff()
      }
      accept(TokenType.RETURN_KEYWORD) -> {
        val returrnNode = returnn()
        if (!accept(TokenType.LINE_BREAK) && !accept(TokenType.SEMICOLON)) {
          error(token, TokenType.SEMICOLON)
        }
        returrnNode
      }
      accept(TokenType.BREAK_KEYWORD) -> breakk()
      accept(TokenType.CONTINUE_KEYWORD) -> continuee()
      accept(TokenType.LINE_BREAK) || accept(TokenType.SEMICOLON) -> {
        ASTNode(Token(), listOf())
      }
      else -> {
        val complexExpressionNode = complexExpression()
        if (!accept(TokenType.LINE_BREAK) && !accept(TokenType.SEMICOLON)) {
          error(token, TokenType.SEMICOLON)
        }
        complexExpressionNode
      }
    }
  }

  fun functionDefinition(): ASTNode {
//    println("function definition")
    val functionDefinitionNode = ASTNode(token, listOf())
    expect(TokenType.IDENTIFIER)
    expect(TokenType.OPEN_PARENTHESIS)
    val argumentsNode = arguments()
    expect(TokenType.CLOSE_PARENTHESIS)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    val instructionNodes = arrayListOf<ASTNode>()
    while (!accept(TokenType.CLOSE_BRACE)) {
      instructionNodes.add(instruction())
    }
    functionDefinitionNode.nodes = listOf(argumentsNode, ASTNode(Token(), instructionNodes))
    expect(TokenType.LINE_BREAK)
    return functionDefinitionNode
  }

  fun arguments(): ASTNode {
    val argumentsNode = ASTNode(Token(), listOf())
    val identifiersNodes = arrayListOf<ASTNode>()
    if (accept(TokenType.IDENTIFIER)) {
      identifiersNodes.add(ASTNode(token, listOf()))
      while (accept(TokenType.COMMA)) {
        expect(TokenType.IDENTIFIER)
        identifiersNodes.add(ASTNode(token, listOf()))
      }
    }
    argumentsNode.nodes = identifiersNodes
    return argumentsNode
  }

  fun callArguments(): ASTNode {
    val callArgumentsNode = ASTNode(Token(), listOf())
    val simpleExpressionsNodes = arrayListOf<ASTNode>()
    simpleExpressionsNodes.add(simpleExpression())
    while (accept(TokenType.COMMA)) {
      simpleExpressionsNodes.add(simpleExpression())
    }
    callArgumentsNode.nodes = simpleExpressionsNodes
    return callArgumentsNode
  }

  fun loop(): ASTNode {
    val loopNode = ASTNode(token, listOf())
    expect(TokenType.OPEN_PARENTHESIS)
    val assignmentNode = assignment()
    expect(TokenType.SEMICOLON)
    val complexExpressionNode = complexExpression()
    expect(TokenType.SEMICOLON)
    val secondAssignmentNode = assignment()
    expect(TokenType.CLOSE_PARENTHESIS)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    val instructionNodes = arrayListOf<ASTNode>()
    while (!accept(TokenType.CLOSE_BRACE)) {
      instructionNodes.add(instruction())
    }
    expect(TokenType.LINE_BREAK)
    loopNode.nodes = listOf(assignmentNode, complexExpressionNode, secondAssignmentNode, ASTNode(Token(), instructionNodes))
    return loopNode
  }

  fun assignment(): ASTNode {
    expect(TokenType.IDENTIFIER)
    val identifierNode = ASTNode(token, listOf())
    expect(TokenType.ASSIGNMENT)
    val assignmentNode = ASTNode(token, listOf())
    val complexExpressionNode = complexExpression()
    assignmentNode.nodes = listOf(identifierNode, complexExpressionNode)
    return assignmentNode
  }

  fun complexExpression(): ASTNode {
//    println("complex expression")
    val firstSimpleExpressionNode = simpleExpression()
    var complexExpressionNode = firstSimpleExpressionNode
    while (true) {
      if (accept(TokenType.ADDITIVE_OPERATOR)) {
        val additiveOperatorNode = ASTNode(token, listOf())
        val simpleExpressionNode = simpleExpression()
        additiveOperatorNode.nodes = listOf(complexExpressionNode, simpleExpressionNode)
        complexExpressionNode = additiveOperatorNode
      } else if (accept(TokenType.ASSIGNMENT)) {
        val assignmentNode = ASTNode(token, listOf())
        val simpleExpressionNode = simpleExpression()
        assignmentNode.nodes = listOf(complexExpressionNode, simpleExpressionNode)
        complexExpressionNode = assignmentNode
      } else {
        break
      }
    }
    return complexExpressionNode
  }

  fun simpleExpression(): ASTNode {
//    println("simple expression")
    var simpleExpressionNode: ASTNode
    when {
      accept(TokenType.NEGATION) -> {
        val negationNode = ASTNode(token, listOf())
        val complexExpressionNode = complexExpression()
        negationNode.nodes = listOf(complexExpressionNode)
        simpleExpressionNode = negationNode
      }
      else -> {
        val valueNode = value()
        simpleExpressionNode = valueNode
        while (true) {
          if (accept(TokenType.MULTIPLICATIVE_OPERATOR)){
            val multiplicativeOperatorNode = ASTNode(token, listOf())
            val secondValueNode = value()
            multiplicativeOperatorNode.nodes = listOf(simpleExpressionNode, secondValueNode)
            simpleExpressionNode = multiplicativeOperatorNode
          } else if (accept(TokenType.RELATIONAL_OPERATOR)) {
            val relationalOperatorNode = ASTNode(token, listOf())
            val secondValueNode = value()
            relationalOperatorNode.nodes = listOf(simpleExpressionNode, secondValueNode)
            simpleExpressionNode = relationalOperatorNode
          } else {
            break
          }
        }
      }
    }
    return simpleExpressionNode
  }

  fun value(): ASTNode {
    var valueNode = ASTNode(Token(), listOf())
    when {
      accept(TokenType.OPEN_PARENTHESIS) -> {
        var parenthesis = 1
        while (accept(TokenType.OPEN_PARENTHESIS)) {
          parenthesis++
        }
        valueNode = complexExpression()
        while (accept(TokenType.CLOSE_PARENTHESIS)) {
          parenthesis--
        }
      }
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
        valueNode = negationNode
      }
      accept(TokenType.LINE_BREAK) -> {
        valueNode = ASTNode(token, listOf())
      }
      accept(TokenType.EOT) -> {
        valueNode = ASTNode(token, listOf())
      }
//      else -> {
//        println("unexpected token")
//        move()
//      }
    }
    return valueNode
  }

  fun returnn(): ASTNode {
//    println("return")
    if (!accept(TokenType.CLOSE_BRACE)) {
      return complexExpression()
    } else {
      return ASTNode(Token(), listOf())
    }
  }

  fun iff(): ASTNode {
//    println("if")
    val iffNode = ASTNode(token, listOf())
    expect(TokenType.OPEN_PARENTHESIS)
    val conditionNode = complexExpression()
    expect(TokenType.CLOSE_PARENTHESIS)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    val instructionsNodes = arrayListOf<ASTNode>()
    while (!accept(TokenType.CLOSE_BRACE)) {
      instructionsNodes.add(instruction())
    }
    if (accept(TokenType.ELSE_KEYWORD)) {
      val elseNode = ASTNode(token, listOf())
      if (accept(TokenType.IF_KEYWORD)) {
        elseNode.nodes = listOf(iff())
      } else {
        expect(TokenType.OPEN_BRACE)
        expect(TokenType.LINE_BREAK)
        val instructionsNodes = arrayListOf<ASTNode>()
        while (!accept(TokenType.CLOSE_BRACE)) {
          instructionsNodes.add(instruction())
        }
        elseNode.nodes = instructionsNodes
        expect(TokenType.LINE_BREAK)
      }
      iffNode.nodes = listOf(conditionNode, ASTNode(Token(), instructionsNodes), elseNode)
    } else {
      expect(TokenType.LINE_BREAK)
      iffNode.nodes = listOf(conditionNode, ASTNode(Token(), instructionsNodes))
    }
    return iffNode
  }

  fun breakk(): ASTNode {
    expect(TokenType.BREAK_KEYWORD)
    return ASTNode(token, listOf())
  }

  fun continuee(): ASTNode {
    expect(TokenType.CONTINUE_KEYWORD)
    return ASTNode(token, listOf())
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