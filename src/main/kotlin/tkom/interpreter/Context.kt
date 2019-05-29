package tkom.interpreter

import tkom.ComplexNumber
import tkom.ast.ASTNode
import tkom.ast.function.ArgumentsListAstNode

class Context private constructor(private val functions: HashMap<String, Pair<ArgumentsListAstNode, ASTNode>>) {

  constructor() : this(HashMap())

  constructor(context: Context) : this(context.functions)

  private val variables = HashMap<String, ComplexNumber>()


  fun deleteVariable(identifier: String) {
    variables.remove(identifier)
  }

  fun setVariable(identifier: String, value: ComplexNumber) {
    variables[identifier] = value
  }

  fun getVariable(identifier: String): ComplexNumber? {
    return variables[identifier]
  }

  fun setFunction(identifier: String, arguments: ArgumentsListAstNode, functionRootNode: ASTNode) {
    functions[identifier] = Pair(arguments, functionRootNode)
  }

  fun getFunction(identifier: String): Pair<ArgumentsListAstNode, ASTNode>? {
    return functions[identifier]
  }
}