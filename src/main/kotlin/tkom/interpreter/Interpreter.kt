package tkom.interpreter

import org.omg.PortableInterceptor.NON_EXISTENT
import tkom.ComplexNumber
import tkom.ComplexNumber.Companion.FALSE
import tkom.ComplexNumber.Companion.TRUE
import tkom.ComplexNumber.Companion.ZERO
import tkom.ast.*
import tkom.ast.binop.BinOpAstNode
import tkom.ast.binop.arithmetic.*
import tkom.ast.binop.comparison.*
import tkom.ast.binop.logical.AndAstNode
import tkom.ast.binop.logical.NegationAstNode
import tkom.ast.binop.logical.OrAstNode
import tkom.ast.function.BuiltInFunctionCallAstNode
import tkom.ast.function.FunctionCallAstNode
import tkom.ast.function.FunctionDefinitionAstNode
import tkom.ast.function.ReturnAstNode
import tkom.ast.loop.BreakAstNode
import tkom.ast.loop.ContinueAstNode
import tkom.ast.loop.LoopAstNode
import tkom.lexer.Lexer
import tkom.parser.Parser
import tkom.semchecker.SemChecker
import tkom.source.Source
import java.lang.Math.sqrt

class Interpreter(source: Source) {

  private val lexer: Lexer = Lexer(source)
  private val parser: Parser
  private var context = Context()
  private val semChecker = SemChecker()

  init {
    parser = Parser(lexer, source)
  }

  fun run() {
    while (true) {
      print(">")
      val (errors, rootNode) = parser.parse()
      if (rootNode is EotNode) {
        break
      } else {
        if (errors > 0) {
          continue
        }
        semChecker.semCheck(rootNode)
        if (semChecker.errorList.size > 0) {
          for (error in semChecker.errorList) {
            println(error)
          }
          semChecker.errorList.clear()
        } else {
          val result = interpret(rootNode)
          if (result is ValueAstNode && result !is NotPrintableValueAstNode) {
            println(result.value)
          }
        }
      }
    }
  }

  private fun interpret(node: ASTNode): ASTNode {
    return when (node) {
      is BinOpAstNode -> {
        executeBinOp(node)
      }
      is ValueAstNode, is NotPrintableValueAstNode -> {
        executeValue(node)
      }
      is AssignmentAstNode -> {
        executeAssignment(node)
      }
      is IdentifierAstNode -> {
        executeIdentifier(node)
      }
      is NegationAstNode -> {
        executeNegation(node)
      }
      is InstructionListAstNode -> {
        executeInstructionList(node)
      }
      is IfAstNode -> {
        executeIf(node)
      }
      is FunctionDefinitionAstNode -> {
        executeFunctionDefinition(node)
      }
      is FunctionCallAstNode -> {
        executeFunctionCall(node)
      }
      is BuiltInFunctionCallAstNode -> {
        executeBuiltInFunction(node)
      }
      is ReturnAstNode -> {
        executeReturn(node)
      }
      is LoopAstNode -> {
        executeLoop(node)
      }
      else -> {
        NopAstNode()
      }
    }
  }

  private fun executeBuiltInFunction(node: BuiltInFunctionCallAstNode): ASTNode {
    when (node.identifier.identifier) {
      "print" -> {
        print((interpret(node.argumentsList.callArguments[0]) as ValueAstNode).value)
      }
      "println" -> {
        println((interpret(node.argumentsList.callArguments[0]) as ValueAstNode).value)
      }
      "sqrt" -> {
        return ValueAstNode(ComplexNumber(sqrt(((interpret(node.argumentsList.callArguments[0]) as ValueAstNode).value).realPart)))
      }
    }
    return NopAstNode()
  }

  private fun executeReturn(node: ReturnAstNode): ASTNode {
    return interpret(node.value)
  }

  private fun executeValue(node: ASTNode): ASTNode {
    return node
  }

  private fun executeIdentifier(node: IdentifierAstNode): ASTNode {
    return ValueAstNode(ComplexNumber(context.getVariable(node.identifier)!!))
  }

  private fun executeNegation(node: NegationAstNode): ASTNode {
    val valueToNegate = (interpret(node.negatedNode) as ValueAstNode).value
    return ValueAstNode(valueToNegate.negate())
  }

  private fun executeLoop(node: LoopAstNode): ASTNode {
    // new context
    val savedVariableIdentifier = ((node.assignment as AssignmentAstNode).identifier as IdentifierAstNode).identifier
    val savedVariable = context.getVariable(savedVariableIdentifier)
    interpret(node.assignment)
    while ((interpret(node.endCondition) as ValueAstNode).value.isTrue()) {
      val result = interpret(node.instructionList)
      if (result is BreakAstNode) {
        break
      }
      interpret(node.stepAssignment)
    }
    // restore context
    if (savedVariable == null) {
      context.deleteVariable(savedVariableIdentifier)
    } else {
      context.setVariable(savedVariableIdentifier, savedVariable)
    }
    return NopAstNode()
  }

  private fun executeFunctionCall(node: FunctionCallAstNode): ASTNode {
    val (arguments, instructionNode) = context.getFunction(node.identifier.identifier)!!
    // new context
    val oldContext = context
    val newContext = Context(oldContext)
    node.argumentsList.callArguments.forEachIndexed { i, argument ->
      run {
        val value = (interpret(argument) as ValueAstNode).value
        newContext.setVariable((arguments.arguments[i] as IdentifierAstNode).identifier, value)
      }
    }
    context = newContext
    val result = interpret(instructionNode)
    val callResult = if (result is ReturnAstNode) {
      interpret(result)
    } else {
      result
    }
    // restore context
    context = oldContext
    return callResult
  }

  private fun executeFunctionDefinition(node: FunctionDefinitionAstNode): ASTNode {
    context.setFunction(node.identifier.identifier, node.arguments, node.instructionList)
    return NopAstNode()
  }

  private fun executeIf(node: IfAstNode): ASTNode {
    var retNode: ASTNode = NopAstNode()
    when {
      (interpret(node.condition) as ValueAstNode).value == TRUE -> {
        val result = interpret(node.instructionList)
        if (result is ReturnAstNode || result is ContinueAstNode || result is BreakAstNode) {
          retNode = result
        }

      }
      node.elseNode != null -> {
        val result = interpret(node.elseNode)
        if (result is ReturnAstNode || result is ContinueAstNode || result is BreakAstNode) {
          retNode = result
        }
      }
    }
    return retNode
  }

  private fun executeInstructionList(node: InstructionListAstNode): ASTNode {
    var retNode: ASTNode = NopAstNode()
    for (instruction in node.instructionList) {
      if (instruction is ReturnAstNode || instruction is ContinueAstNode || instruction is BreakAstNode) {
        retNode = instruction
        break
      }
      retNode = interpret(instruction)
      if (retNode is ReturnAstNode || retNode is ContinueAstNode || retNode is BreakAstNode) {
        break
      }
    }
    return retNode
  }

  private fun executeAssignment(node: AssignmentAstNode): ASTNode {
    val possibleValue = interpret(node.value)
    return if (possibleValue is NopAstNode) {
      context.setVariable((node.identifier as IdentifierAstNode).identifier, ZERO)
      NotPrintableValueAstNode(ZERO)
    } else {
      val valueNode = (possibleValue as ValueAstNode)
      context.setVariable((node.identifier as IdentifierAstNode).identifier, valueNode.value)
      NotPrintableValueAstNode(valueNode.value)
    }
  }

  private fun executeBinOp(node: BinOpAstNode): ASTNode {
    val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
    val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
    return when (node) {
      is AddAstNode -> {
        ValueAstNode(leftValue.add(rightValue))
      }
      is SubtractAstNode -> {
        ValueAstNode(leftValue.subtract(rightValue))
      }
      is MultiplyAstNode -> {
        ValueAstNode(leftValue.multiply(rightValue))
      }
      is DivideAstNode -> {
        ValueAstNode(leftValue.divide(rightValue))
      }
      is ModuloAstNode -> {
        ValueAstNode(leftValue.modulo(rightValue))
      }
      is PowerAstNode -> {
        ValueAstNode(leftValue.power(rightValue))
      }
      is AndAstNode -> {
        ValueAstNode(leftValue.and(rightValue))
      }
      is OrAstNode -> {
        ValueAstNode(leftValue.or(rightValue))
      }
      is GreaterAstNode -> {
        if (leftValue > rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is GreaterOrEqualAstNode -> {
        if (leftValue >= rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is LessAstNode -> {
        if (leftValue < rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is LessOrEqualAstNode -> {
        if (leftValue <= rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is EqualAstNode -> {
        if (leftValue == rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is NotEqualAstNode -> {
        if (leftValue != rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      else -> {
        NopAstNode()
      }
    }
  }

  fun singleTestRun(): ComplexNumber {
    var resultNumber = ComplexNumber.ZERO
    while (true) {
      val (errors, rootNode) = parser.parse()
      if (rootNode is EotNode) {
        break
      } else {
        if (errors > 0) {
          continue
        }
        semChecker.semCheck(rootNode)
        if (semChecker.errorList.size > 0) {
          for (error in semChecker.errorList) {
            println(error)
          }
          semChecker.errorList.clear()
        } else {
          val result = interpret(rootNode)
          if (result is ValueAstNode && result !is NotPrintableValueAstNode) {
            resultNumber = result.value
          }
        }
      }
    }
    return resultNumber
  }
}