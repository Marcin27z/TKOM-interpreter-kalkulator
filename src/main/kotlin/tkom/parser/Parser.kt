package tkom.parser

import tkom.Token
import tkom.TokenType
import tkom.ast.*
import tkom.ast.Arithmetic.AddAstNode
import tkom.ast.Arithmetic.DivideAstNode
import tkom.ast.Arithmetic.MultiplyAstNode
import tkom.ast.Arithmetic.SubtractAstNode
import tkom.ast.Comparison.*
import tkom.ast.Logical.AndAstNode
import tkom.ast.Logical.FunctionCallAstNode
import tkom.ast.Logical.NegationAstNode
import tkom.ast.Logical.OrAstNode
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
        functionDefinition()
      }
      accept(TokenType.LINE_BREAK) -> {
        operation()
      }
      else -> {
        instruction()
      }
    }
  }

  fun loopInstruction(): ASTNode {
    return when {
      accept(TokenType.BREAK_KEYWORD) -> breakk()
      accept(TokenType.CONTINUE_KEYWORD) -> continuee()
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

      accept(TokenType.LINE_BREAK) || accept(TokenType.SEMICOLON) -> {
        NopAstNode()
      }
      else -> {
        val complexExpressionNode = comparisonExpression()
        if (!accept(TokenType.LINE_BREAK) && !accept(TokenType.SEMICOLON)) {
          error(token, TokenType.SEMICOLON)
        }
        complexExpressionNode
      }
    }
  }

  fun functionDefinition(): ASTNode {
//    println("function definition")
    expect(TokenType.IDENTIFIER)
    val identifierNode = IdentifierAstNode(token.value)
    expect(TokenType.OPEN_PARENTHESIS)
    val argumentsNode = arguments()
    expect(TokenType.CLOSE_PARENTHESIS)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    val instructionNodes = arrayListOf<ASTNode>()
    while (!accept(TokenType.CLOSE_BRACE)) {
      instructionNodes.add(instruction())
    }
    expect(TokenType.LINE_BREAK)
    return FunctionDefinitionAstNode(identifierNode, argumentsNode, InstructionListAstNode(instructionNodes))
  }

  fun arguments(): ASTNode {
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

  fun callArguments(): ASTNode {
    val callArguments = arrayListOf<ASTNode>()
    callArguments.add(comparisonExpression())
    while (accept(TokenType.COMMA)) {
      callArguments.add(comparisonExpression())
    }
    return CallArgumentsListAstNode(callArguments)
  }

  fun loop(): ASTNode {
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
      instructionNodes.add(loopInstruction())
    }
    expect(TokenType.LINE_BREAK)
    return LoopAstNode(assignmentNode, comparisonExpressionNode, secondAssignmentNode, InstructionListAstNode(instructionNodes))
  }

  fun assignment(): ASTNode {
    expect(TokenType.IDENTIFIER)
    val identifierNode = IdentifierAstNode(token.value)
    expect(TokenType.ASSIGNMENT)
    val complexExpressionNode = comparisonExpression()
    return AssignmentAstNode(identifierNode, complexExpressionNode)
  }

  fun comparisonExpression(): ASTNode {
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

  fun logicalExpression(): ASTNode {
    val firstComplexExpressionNode = complexExpression()
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


  fun complexExpression(): ASTNode {
//    println("complex expression")
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

  fun simpleExpression(): ASTNode {
//    println("simple expression")
    var simpleExpressionNode: ASTNode
    when {
      accept(TokenType.NEGATION) -> {
        simpleExpressionNode = NegationAstNode(comparisonExpression())
      }
      else -> {
        val valueNode = value()
        simpleExpressionNode = valueNode
        while (true) {
          if (accept(TokenType.MULTIPLICATIVE_OPERATOR)) {
            val operatorToken = token
            val secondValueNode = value()
            if (operatorToken.value == "*") {
              simpleExpressionNode = MultiplyAstNode(simpleExpressionNode, secondValueNode)
            } else if (operatorToken.value == "/") {
              simpleExpressionNode = DivideAstNode(simpleExpressionNode, secondValueNode)
            }

          } else {
            break
          }
        }
      }
    }
    return simpleExpressionNode
  }

  fun value(): ASTNode {
    return when {
      accept(TokenType.OPEN_PARENTHESIS) -> {
        var parenthesis = 1
        while (accept(TokenType.OPEN_PARENTHESIS)) {
          parenthesis++
        }
        val valueNode = comparisonExpression()
        while (accept(TokenType.CLOSE_PARENTHESIS)) {
          parenthesis--
        }
        valueNode
      }
      accept(TokenType.NUMBER) -> {
        ValueAstNode(token.complexNumber)
      }
      accept(TokenType.IDENTIFIER) -> {
        val identifierNode = IdentifierAstNode(token.value)
        when {
          accept(TokenType.OPEN_PARENTHESIS) -> {
            val argumentsNode = callArguments()
            expect(TokenType.CLOSE_PARENTHESIS)
            FunctionCallAstNode(identifierNode, argumentsNode)
          }
          accept(TokenType.ASSIGNMENT) -> {
            val complexExpressionNode = comparisonExpression()
            AssignmentAstNode(identifierNode, complexExpressionNode)
          }
          else -> identifierNode
        }
      }
      accept(TokenType.NEGATION) -> {
        NegationAstNode(value())
      }
      accept(TokenType.LINE_BREAK) -> {
        NopAstNode()
      }
      accept(TokenType.EOT) -> {
        NopAstNode()
      }
      else -> {
        NopAstNode()
      }
    }
  }

  fun returnn(): ASTNode {
//    println("return")
    return if (!accept(TokenType.CLOSE_BRACE)) {
      ReturnAstNode(comparisonExpression())
    } else {
      ReturnAstNode()
    }
  }

  fun iff(): ASTNode {
//    println("if")
    expect(TokenType.OPEN_PARENTHESIS)
    val conditionNode = comparisonExpression()
    expect(TokenType.CLOSE_PARENTHESIS)
    expect(TokenType.OPEN_BRACE)
    expect(TokenType.LINE_BREAK)
    val instructionsNodes = arrayListOf<ASTNode>()
    while (!accept(TokenType.CLOSE_BRACE)) {
      instructionsNodes.add(instruction())
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
          instructionsNodes.add(instruction())
        }
        elseNode = InstructionListAstNode(instructionsNodes)
        expect(TokenType.LINE_BREAK)
      }
      IfAstNode(conditionNode, InstructionListAstNode(instructionsNodes), elseNode)
    } else {
      expect(TokenType.LINE_BREAK)
      IfAstNode(conditionNode, InstructionListAstNode(instructionsNodes))
    }
  }

  fun breakk(): ASTNode {
    expect(TokenType.BREAK_KEYWORD)
    return BreakAstNode()
  }

  fun continuee(): ASTNode {
    expect(TokenType.CONTINUE_KEYWORD)
    return ContinueAstNode()
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