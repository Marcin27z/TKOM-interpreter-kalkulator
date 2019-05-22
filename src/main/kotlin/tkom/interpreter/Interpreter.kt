package tkom.interpreter

import tkom.ComplexNumber
import tkom.ComplexNumber.Companion.FALSE
import tkom.ComplexNumber.Companion.TRUE
import tkom.ast.*
import tkom.ast.Arithmetic.*
import tkom.ast.Comparison.*
import tkom.ast.Logical.AndAstNode
import tkom.ast.FunctionCallAstNode
import tkom.ast.Logical.NegationAstNode
import tkom.ast.Logical.OrAstNode
import tkom.lexer.Lexer
import tkom.parser.Parser
import tkom.semchecker.SemChecker
import tkom.source.Source

class Interpreter(source: Source) {

  private val lexer: Lexer = Lexer(source)
  private val parser: Parser
  private val context = Context()
  private val semChecker = SemChecker()

  init {
    parser = Parser(lexer, source)
  }

  fun run() {
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
            println(result.value)
          }
        }
      }
    }
  }

  private fun interpret(node: ASTNode): ASTNode {
    return when (node) {
      is AddAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        ValueAstNode(leftValue.add(rightValue))
      }
      is SubtractAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        ValueAstNode(leftValue.subtract(rightValue))
      }
      is MultiplyAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        ValueAstNode(leftValue.multiply(rightValue))
      }
      is DivideAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        ValueAstNode(leftValue.divide(rightValue))
      }
      is ModuloAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        ValueAstNode(leftValue.modulo(rightValue))
      }
      is PowerAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        ValueAstNode(leftValue.power(rightValue))
      }
      is ValueAstNode -> {
        node
      }
      is NotPrintableValueAstNode -> {
        node
      }
      is GreaterAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        if (leftValue > rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is GreaterOrEqualAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        if (leftValue >= rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is LessAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        if (leftValue < rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is LessOrEqualAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        if (leftValue <= rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is EqualAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        if (leftValue == rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is NotEqualAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        if (leftValue != rightValue) {
          ValueAstNode(TRUE)
        } else {
          ValueAstNode(FALSE)
        }
      }
      is AssignmentAstNode -> {
        val valueNode = (interpret(node.value) as ValueAstNode)
        context.setVariable((node.identifier as IdentifierAstNode).identifier, valueNode.value)
        return NotPrintableValueAstNode(valueNode.value)
      }
      is IdentifierAstNode -> {
        return ValueAstNode(ComplexNumber(context.getVariable(node.identifier)!!))
      }
      is AndAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        ValueAstNode(leftValue.and(rightValue))
      }
      is OrAstNode -> {
        val leftValue = (interpret(node.leftOperand) as ValueAstNode).value
        val rightValue = (interpret(node.rightOperand) as ValueAstNode).value
        ValueAstNode(leftValue.or(rightValue))
      }
      is NegationAstNode -> {
        val valueToNegate = (interpret(node.negatedNode) as ValueAstNode).value
        ValueAstNode(valueToNegate.negate())
      }
      is InstructionListAstNode -> {
        var retNode: ASTNode = NopAstNode()
        for (instruction in node.instructionList) {
          retNode = interpret(instruction)
        }
        retNode
      }
      is IfAstNode -> {
        when {
          (interpret(node.condition) as ValueAstNode).value == TRUE -> {
            interpret(node.instructionList)
            NopAstNode()
          }
          node.elseNode != null -> {
            interpret(node.elseNode)
            NopAstNode()
          }
          else -> NopAstNode()
        }
      }
      is FunctionDefinitionAstNode -> {
        context.setFunction((node.identifier as IdentifierAstNode).identifier, node.arguments, node.instructionList)
        NopAstNode()
      }
      is FunctionCallAstNode -> {
        val (arguments, instructionNode) = context.getFunction((node.identifier as IdentifierAstNode).identifier)!!
        // new context
        (node.argumentsList as CallArgumentsListAstNode).callArguments.forEachIndexed { i, argument ->
          run {
            val value = (interpret(argument) as ValueAstNode).value
            context.setVariable(((arguments as ArgumentsListAstNode).arguments[i] as IdentifierAstNode).identifier, value)
          }
        }
        interpret(instructionNode)
        // restore context
      }
      is ReturnAstNode -> {
        interpret(node.value)
      }
      is LoopAstNode -> {
        // new context
        interpret(node.assignment)
        while ((interpret(node.endCondition) as ValueAstNode).value.isTrue()) {
          interpret(node.instructionList)
          interpret(node.stepAssignment)
        }
        // restore context
        NopAstNode()
      }
      else -> {
        NopAstNode()
      }
    }
  }


}