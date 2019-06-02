package tkom.semchecker

import tkom.ast.*
import tkom.ast.binop.BinOpAstNode
import tkom.ast.binop.logical.NegationAstNode
import tkom.ast.function.*
import tkom.ast.loop.BreakAstNode
import tkom.ast.loop.ContinueAstNode
import tkom.ast.loop.LoopAstNode

class SemChecker private constructor(private val builtInFunction: HashMap<String, Int>) {

  constructor() : this(HashMap())

  init {
    initBuiltInFunctions()
  }

  private var assignedVariablesSet = HashSet<String>()
  private val definedFunctionsMap = HashMap<String, Int>()
  private val errorList = arrayListOf<String>()

  private fun initBuiltInFunctions() {
    builtInFunction["print"] = 1
    builtInFunction["println"] = 1
    builtInFunction["sqrt"] = 1
  }

  private fun getBuiltInFunctionArgumentNumber(identifier: String): Int? {
    return builtInFunction[identifier]
  }

  fun cancelAssignment(variable: String) {
    assignedVariablesSet.remove(variable)
  }

  fun getErrorList(): ArrayList<String> {
    return errorList
  }

  fun clearErrorList() {
    errorList.clear()
  }

  fun semCheck(node: ASTNode, insideLoop: Boolean = false, insideFunction: Boolean = false) {
    when (node) {
      is BinOpAstNode -> {
        semCheck(node.leftOperand, insideLoop, insideFunction)
        semCheck(node.rightOperand, insideLoop, insideFunction)
      }
      is AssignmentAstNode -> {
        semCheck(node.value, insideLoop, insideFunction)
        if (node.identifier is IdentifierAstNode) {
          assignedVariablesSet.add((node.identifier).identifier)
        } else {
          errorList.add("Cannot assign value to value")
        }
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
        val oldAssignedVariablesSet = assignedVariablesSet
        assignedVariablesSet = HashSet()
        for (argument in node.arguments.arguments) {
          argumentsHashSet.add((argument as IdentifierAstNode).identifier)
        }
        val assignedVariablesSetOutside = HashSet(assignedVariablesSet)
        assignedVariablesSet.addAll(argumentsHashSet)
        definedFunctionsMap[node.identifier.identifier]  = (node.arguments).arguments.size
        semCheck(node.instructionList, insideLoop, true)
        assignedVariablesSet = assignedVariablesSetOutside
        if (errorCountBefor != errorList.size) {
          definedFunctionsMap.remove((node.identifier).identifier)
        }
        assignedVariablesSet = oldAssignedVariablesSet

      }
      is BuiltInFunctionCallAstNode -> {
        val functionIdentifier = node.identifier.identifier
        val argumentNumber = getBuiltInFunctionArgumentNumber(functionIdentifier)
        when {
          argumentNumber == null -> errorList.add("No function with name \"$functionIdentifier\" defined")
          argumentNumber < node.argumentsList.callArguments.size -> errorList.add("Too many arguments for function \"$functionIdentifier\"")
          argumentNumber > (node.argumentsList).callArguments.size -> errorList.add("Too few arguments for function \"$functionIdentifier\"")
        }

      }
      is FunctionCallAstNode -> {
        val functionIdentifier = node.identifier.identifier
        val arguments = node.argumentsList.callArguments
        for (argument in arguments) {
          semCheck(argument)
        }
        val functionArgumentsNumber = arguments.size
        if (!definedFunctionsMap.contains(functionIdentifier) || definedFunctionsMap[functionIdentifier] != functionArgumentsNumber) {
          errorList.add("No function with name \"$functionIdentifier\" and $functionArgumentsNumber arguments defined")
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
        val iteratorVariableIdentifier = ((node.assignment as AssignmentAstNode).identifier as IdentifierAstNode).identifier
        val iteratorVariablePreviouslyAssigned = assignedVariablesSet.contains(iteratorVariableIdentifier)
        semCheck(node.assignment, insideLoop, insideFunction)
        semCheck(node.endCondition, insideLoop, insideFunction)
        semCheck(node.instructionList, true, insideFunction)
        semCheck(node.stepAssignment, insideLoop, insideFunction)
        if (!iteratorVariablePreviouslyAssigned) {
          assignedVariablesSet.remove(iteratorVariableIdentifier)
        }
      }
      is ContinueAstNode -> {
        if (!insideLoop) {
          errorList.add("Unexpected continue statement")
        }
      }
      is BreakAstNode -> {
        if (!insideLoop) {
          errorList.add("Unexpected break statement")
        }
      }
      else -> {

      }
    }
  }
}