package tkom.semchecker

import tkom.ComplexNumber
import tkom.ast.*
import tkom.ast.Comparison.*
import tkom.ast.Logical.AndAstNode
import tkom.ast.FunctionCallAstNode
import tkom.ast.Logical.NegationAstNode
import tkom.ast.Logical.OrAstNode

class SemChecker {

  private var assignedVariablesSet = HashSet<String>()
  private val definedFunctionsSet = HashSet<Pair<String, Int>>()
  val errorList = arrayListOf<String>()

  fun semCheck(node: ASTNode, insideLoop: Boolean = false, insideFunction: Boolean = false) {
    when (node) {
      is BinOpAstNode -> {
        semCheck(node.leftOperand, insideLoop, insideFunction)
        semCheck(node.rightOperand, insideLoop, insideFunction)
      }
      is AssignmentAstNode -> {
        assignedVariablesSet.add((node.identifier as IdentifierAstNode).identifier)
        semCheck(node.value, insideLoop, insideFunction)
      }
      is IdentifierAstNode -> {
        val identifier = (node.identifier)
        if (!assignedVariablesSet.contains(identifier)) {
          errorList.add("Variable $identifier is not initialized")
        } else {

        }
      }
      is NegationAstNode -> {
        semCheck(node.negatedNode, insideLoop, insideFunction)
      }
      is InstructionListAstNode -> {
        for (instruction in node.instructionList) {
          semCheck(instruction, insideLoop, insideFunction)
        }
      }
      is IfAstNode -> {
        semCheck(node.condition, insideLoop, insideFunction)
        semCheck(node.instructionList, insideLoop, insideFunction)
        if (node.elseNode != null) {
          semCheck(node.elseNode, insideLoop, insideFunction)
        }
      }
      is FunctionDefinitionAstNode -> {
        val errorCountBefor = errorList.size
        val argumentsHashSet = HashSet<String>()
        for (argument in (node.arguments  as ArgumentsListAstNode).arguments) {
          argumentsHashSet.add((argument as IdentifierAstNode).identifier)
        }
        val assignedVariablesSetOutside = HashSet(assignedVariablesSet)
        assignedVariablesSet.addAll(argumentsHashSet)
        semCheck(node.instructionList, insideLoop, true)
        assignedVariablesSet = assignedVariablesSetOutside
        if (errorCountBefor == errorList.size) {
          definedFunctionsSet.add(Pair((node.identifier as IdentifierAstNode).identifier, (node.arguments).arguments.size))
        }

      }
      is FunctionCallAstNode -> {
        val functionIdentifier = (node.identifier as IdentifierAstNode).identifier
        if (!definedFunctionsSet.contains(Pair(functionIdentifier, (node.argumentsList as CallArgumentsListAstNode).callArguments.size))) {
          errorList.add("No function with name \"$functionIdentifier\" defined")
        }
      }
      is ReturnAstNode -> {
        if (insideFunction) {
          semCheck(node.value, insideLoop, insideFunction)
        } else {
          errorList.add("Unexpected return statement")
        }
      }
      is LoopAstNode -> {
        semCheck(node.assignment, insideLoop, insideFunction)
        semCheck(node.endCondition, insideLoop, insideFunction)
        semCheck(node.instructionList, true, insideFunction)
        semCheck(node.stepAssignment, insideLoop, insideFunction)
      }
      is ContinueAstNode -> {
        if (!insideLoop) {

        }
      }
      is BreakAstNode -> {
        if (!insideLoop) {

        }
      }

      else -> {

      }
    }
  }
}