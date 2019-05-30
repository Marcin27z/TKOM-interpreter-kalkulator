package tkom.parser

import tkom.ComplexNumber
import tkom.Token
import tkom.TokenType
import tkom.ast.*
import tkom.ast.binop.arithmetic.*
import tkom.ast.binop.comparison.*
import tkom.ast.binop.logical.AndAstNode
import tkom.ast.binop.logical.NegationAstNode
import tkom.ast.binop.logical.OrAstNode
import tkom.ast.function.*
import tkom.ast.loop.BreakAstNode
import tkom.ast.loop.ContinueAstNode
import tkom.ast.loop.LoopAstNode
import tkom.lexer.Lexer
import tkom.source.Source

class Parser(private val lexer: Lexer, private val source: Source) {

  lateinit var token: Token
  private var moveFlag = true
  private var errors = 0
  private var exitFlag = false
  private val errorList = arrayListOf<ParseError>()

  private fun accept(symbol: TokenType): Boolean {
    advance()
    if (token.tokenType == symbol) {
      if (token.tokenType == TokenType.EOT) {
        exitFlag = true
        advance()
      }
      if (!exitFlag)
        move()
      return true
    }
    return false
  }

  private fun expect(symbol: TokenType): Boolean {
    if (accept(symbol)) {
      return true
    }
    error(token, symbol)
    move()
    advance()
    return false
  }

  private fun advance() {
    if (moveFlag) {
      token = lexer.getToken()
      moveFlag = false
    }
  }

  private fun move() {
    moveFlag = true
  }

  fun parse(): Pair<Int, ASTNode> {
    val rootNode = operations()
    for (error in errorList) {
      error.printError(source.getRawInput())
    }
    errors = errorList.size
    source.reset()
    errorList.clear()
    return Pair(errors, rootNode)
  }

  private fun operations(): ASTNode {
    return when {
      accept(TokenType.FUN_KEYWORD) -> {
        functionDefinition()
      }
      accept(TokenType.LINE_BREAK) -> {
        NopAstNode()
      }
      else -> {
        operation()
      }
    }
  }

  private fun operation(): ASTNode {
    return when {
      accept(TokenType.LINE_BREAK) -> {
        NopAstNode()
      }
      else -> {
        var operationNode = instruction()
          if (!accept(TokenType.LINE_BREAK) && operationNode !is EotNode) {
            error(token, TokenType.LINE_BREAK)
            operationNode = InstructionListAstNode(arrayListOf(operationNode, operation()))
          }
        operationNode
      }
    }

  }

  private fun instruction(): ASTNode {
    return when {
      accept(TokenType.BREAK_KEYWORD) -> breakk()
      accept(TokenType.CONTINUE_KEYWORD) -> continuee()
      accept(TokenType.FOR_KEYWORD) -> {
        loop()
      }
      accept(TokenType.IF_KEYWORD) -> {
        iff()
      }
      accept(TokenType.RETURN_KEYWORD) -> {
        returnn()
      }

      accept(TokenType.LINE_BREAK) || accept(TokenType.SEMICOLON) -> {
        NopAstNode()
      }
      else -> {
        val complexExpressionNode = assignment()
        complexExpressionNode
      }
    }
  }

  private fun functionDefinition(): ASTNode {
    expect(TokenType.IDENTIFIER)
    val identifierNode = IdentifierAstNode(token.value)
    expect(TokenType.OPEN_PARENTHESIS)
    val argumentsNode = arguments() as ArgumentsListAstNode
    accept(TokenType.CLOSE_PARENTHESIS)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    val instructionNodes = arrayListOf<ASTNode>()
    while (!accept(TokenType.CLOSE_BRACE)) {
      instructionNodes.add(operation())
    }
    expect(TokenType.LINE_BREAK)
    return FunctionDefinitionAstNode(identifierNode, argumentsNode, InstructionListAstNode(instructionNodes))
  }

  private fun arguments(): ASTNode {
    val identifiersNodes = arrayListOf<ASTNode>()
    if (accept(TokenType.IDENTIFIER)) {
      identifiersNodes.add(IdentifierAstNode(token.value))
      while (accept(TokenType.COMMA)) {
        expect(TokenType.IDENTIFIER)
        identifiersNodes.add(IdentifierAstNode(token.value))
      }
    }
    return ArgumentsListAstNode(identifiersNodes)
  }

  private fun callArguments(): ASTNode {
    val callArguments = arrayListOf<ASTNode>()
    val firstCallArgument = comparisonExpression()
    if (firstCallArgument !is NopAstNode) {
      callArguments.add(firstCallArgument)
      while (accept(TokenType.COMMA)) {
        callArguments.add(comparisonExpression())
      }
    }
    return CallArgumentsListAstNode(callArguments)
  }

  private fun loop(): ASTNode {
    expect(TokenType.OPEN_PARENTHESIS)
    val assignmentNode = assignment()
    expect(TokenType.SEMICOLON)
    val comparisonExpressionNode = comparisonExpression()
    expect(TokenType.SEMICOLON)
    val secondAssignmentNode = assignment()
    expect(TokenType.CLOSE_PARENTHESIS)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    val instructionNodes = arrayListOf<ASTNode>()
    while (!accept(TokenType.CLOSE_BRACE)) {
      instructionNodes.add(operation())
    }
    return LoopAstNode(assignmentNode, comparisonExpressionNode, secondAssignmentNode, InstructionListAstNode(instructionNodes))
  }

  private fun assignment(): ASTNode {
    val identifierNode = comparisonExpression()
    var assignmentNode = identifierNode
    if (accept(TokenType.ASSIGNMENT)) {
      val value = assignment()
      assignmentNode = AssignmentAstNode(identifierNode, value)
    }
    return assignmentNode
  }

  private fun comparisonExpression(): ASTNode {
    val firstLogicalExpressionNode = logicalExpression()
    var comparisonExpressionNode = firstLogicalExpressionNode
    if (accept(TokenType.RELATIONAL_OPERATOR)) {
      val operatorToken = token
      val secondLogicalExpressionNode = comparisonExpression()
      when {
        operatorToken.value == "<" -> {
          comparisonExpressionNode = LessAstNode(firstLogicalExpressionNode, secondLogicalExpressionNode)
        }
        operatorToken.value == ">" -> {
          comparisonExpressionNode = GreaterAstNode(firstLogicalExpressionNode, secondLogicalExpressionNode)
        }
        operatorToken.value == "<=" -> {
          comparisonExpressionNode = LessOrEqualAstNode(firstLogicalExpressionNode, secondLogicalExpressionNode)
        }
        operatorToken.value == ">=" -> {
          comparisonExpressionNode = GreaterOrEqualAstNode(firstLogicalExpressionNode, secondLogicalExpressionNode)
        }
        operatorToken.value == "!=" -> {
          comparisonExpressionNode = NotEqualAstNode(firstLogicalExpressionNode, secondLogicalExpressionNode)
        }
        operatorToken.value == "==" -> {
          comparisonExpressionNode = EqualAstNode(firstLogicalExpressionNode, secondLogicalExpressionNode)
        }
      }
    }
    return comparisonExpressionNode
  }

  private fun logicalExpression(): ASTNode {
    val firstComplexExpressionNode = powerExpression()
    var logicalExpressionNode = firstComplexExpressionNode
    if (accept(TokenType.LOGICAL_AND)) {
      val secondComplexExpression = logicalExpression()
      logicalExpressionNode = AndAstNode(firstComplexExpressionNode, secondComplexExpression)
    } else if (accept(TokenType.LOGICAL_OR)) {
      val secondComplexExpression = logicalExpression()
      logicalExpressionNode = OrAstNode(firstComplexExpressionNode, secondComplexExpression)
    }
    return logicalExpressionNode
  }

  private fun powerExpression(): ASTNode {
    val firstComplexExpressionNode = complexExpression()
    var powerExpressionNode = firstComplexExpressionNode
    if (accept(TokenType.POWER_OPERATOR)) {
      val secondComplexExpressionNode = powerExpression()
      powerExpressionNode = PowerAstNode(firstComplexExpressionNode, secondComplexExpressionNode)
    }
    return powerExpressionNode
  }


  private fun complexExpression(): ASTNode {
    val firstSimpleExpressionNode = simpleExpression()
    var complexExpressionNode = firstSimpleExpressionNode
    if (accept(TokenType.ADDITIVE_OPERATOR)) {
      val operatorToken = token
      val simpleExpressionNode = complexExpression()
      if (operatorToken.value == "+") {
        complexExpressionNode = AddAstNode(firstSimpleExpressionNode, simpleExpressionNode)
      } else if (operatorToken.value == "-") {
        complexExpressionNode = SubtractAstNode(firstSimpleExpressionNode, simpleExpressionNode)
      }
    }
    return complexExpressionNode
  }

  private fun simpleExpression(): ASTNode {
    var simpleExpressionNode: ASTNode
    val valueNode = value()
    simpleExpressionNode = valueNode
    if (accept(TokenType.MULTIPLICATIVE_OPERATOR)) {
      val operatorToken = token
      val secondValueNode = simpleExpression()
      when {
        operatorToken.value == "*" -> simpleExpressionNode = MultiplyAstNode(simpleExpressionNode, secondValueNode)
        operatorToken.value == "/" -> simpleExpressionNode = DivideAstNode(simpleExpressionNode, secondValueNode)
        operatorToken.value == "%" -> simpleExpressionNode = ModuloAstNode(simpleExpressionNode, secondValueNode)
      }
    }
    return simpleExpressionNode
  }

  private fun value(): ASTNode {
    return when {
      accept(TokenType.OPEN_PARENTHESIS) -> {
        var parenthesis = 1
        while (accept(TokenType.OPEN_PARENTHESIS)) {
          parenthesis++
        }
        val valueNode = assignment()
        while (accept(TokenType.CLOSE_PARENTHESIS)) {
          parenthesis--
        }
        valueNode
      }
      accept(TokenType.NUMBER) -> {
        ValueAstNode(token.complexNumber)
      }
      accept(TokenType.BUILT_IN_FUNCTION) -> {
        val identifierNode = IdentifierAstNode(token.value)
        expect(TokenType.OPEN_PARENTHESIS)
        val argumentsNode = callArguments() as CallArgumentsListAstNode
        accept(TokenType.CLOSE_PARENTHESIS)
        BuiltInFunctionCallAstNode(identifierNode, argumentsNode)
      }
      accept(TokenType.IDENTIFIER) -> {
        val identifierNode = IdentifierAstNode(token.value)
        when {
          accept(TokenType.OPEN_PARENTHESIS) -> {
            val argumentsNode = callArguments() as CallArgumentsListAstNode
            accept(TokenType.CLOSE_PARENTHESIS)
            FunctionCallAstNode(identifierNode, argumentsNode)
          }
          else -> identifierNode
        }
      }
      accept(TokenType.NEGATION) -> {
        NegationAstNode(comparisonExpression())
      }
      accept(TokenType.LINE_BREAK) -> {
        NopAstNode()
      }
      accept(TokenType.EOT) -> {
        EotNode()
      }
      accept(TokenType.ADDITIVE_OPERATOR) -> {
        val valueNode: ASTNode
        val operatorToken = token
        val value = value()
        if (operatorToken.value == "+") {
          valueNode = AddAstNode(ValueAstNode(ComplexNumber.ZERO), value)
        } else {
          valueNode = SubtractAstNode(ValueAstNode(ComplexNumber.ZERO), value)
        }
        valueNode
      }
      accept(TokenType.CLOSE_PARENTHESIS) -> {
        NopAstNode()
      }
      else -> {
        errorList.add(UnexpectedTokenError(token))
        move()
        advance()
        NopAstNode()
      }
    }
  }

  private fun returnn(): ASTNode {
    return if (!accept(TokenType.CLOSE_BRACE)) {
      ReturnAstNode(assignment())
    } else {
      ReturnAstNode()
    }
  }

  private fun iff(): ASTNode {
    expect(TokenType.OPEN_PARENTHESIS)
    val conditionNode = assignment()
    expect(TokenType.CLOSE_PARENTHESIS)
    accept(TokenType.LINE_BREAK)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    val instructionsNodes = arrayListOf<ASTNode>()
    while (!accept(TokenType.CLOSE_BRACE)) {
      instructionsNodes.add(operation())
    }
    return if (accept(TokenType.ELSE_KEYWORD)) {
      val elseNode: ASTNode
      if (accept(TokenType.IF_KEYWORD)) {
        elseNode = iff()
      } else {
        expect(TokenType.OPEN_BRACE)
        expect(TokenType.LINE_BREAK)
        val instructionsNodes = arrayListOf<ASTNode>()
        while (!accept(TokenType.CLOSE_BRACE)) {
          instructionsNodes.add(operation())
        }
        elseNode = InstructionListAstNode(instructionsNodes)
      }
      IfAstNode(conditionNode, InstructionListAstNode(instructionsNodes), elseNode)
    } else {
      IfAstNode(conditionNode, InstructionListAstNode(instructionsNodes))
    }
  }

  private fun breakk(): ASTNode {
    return BreakAstNode()
  }

  private fun continuee(): ASTNode {
    return ContinueAstNode()
  }

  private fun error(token: Token, tokenType: TokenType) {
    errorList.add(ParseError(token, tokenType))
  }
}

open class ParseError(
    private val token: Token,
    private val tokenType: TokenType
) {

  open fun printError(sourceCharacters: String) {
    val line = token.position.line
    val sourceLine = sourceCharacters.split("\n")[line]
    println(sourceLine)
    repeat(token.position.column - 1) {
      print(" ")
    }
    println("^")
    println("line: ${token.position.line}, column: ${token.position.column} expected $tokenType")
  }
}

class UnexpectedTokenError(private val token: Token) : ParseError(token, token.tokenType) {
  override fun printError(sourceCharacters: String) {
    val line = token.position.line
    val sourceLine = sourceCharacters.split("\n")[line]
    println(sourceLine)
    repeat(token.position.column - 1) {
      print(" ")
    }
    println("^")
    println("line: ${token.position.line}, column: ${token.position.column} unexpected token: ${token.value}")
  }
}