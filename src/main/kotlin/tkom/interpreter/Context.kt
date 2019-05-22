package tkom.interpreter

import tkom.ComplexNumber
import tkom.ast.ASTNode

class Context : Cloneable {

  private val variables = HashMap<String, ComplexNumber>()
  private val functions = HashMap<String, Pair<ASTNode, ASTNode>>()

  fun setVariable(identifier: String, value: ComplexNumber) {
    variables[identifier] = value
  }

  fun getVariable(identifier: String): ComplexNumber? {
    return variables[identifier]
  }

  fun setFunction(identifier: String, arguments: ASTNode, functionRootNode: ASTNode) {
    functions[identifier] = Pair(arguments, functionRootNode)
  }

  fun getFunction(identifier: String): Pair<ASTNode, ASTNode>? {
    return functions[identifier]
  }

  override fun clone(): Any {
    val context = Context()
    for (variable in variables) {
      context.setVariable(variable.key, variable.value)
    }
    return context
  }
}