package tkom.interpreter

import tkom.ComplexNumber

class Context : Cloneable {

  private val variables = HashMap<String, ComplexNumber>()

  fun setVariable(identifier: String, value: ComplexNumber) {
    variables[identifier] = value
  }

  fun getVariable(identifier: String): ComplexNumber? {
    return variables[identifier]
  }

  override fun clone(): Any {
    val context = Context()
    for (variable in variables) {
      context.setVariable(variable.key, variable.value)
    }
    return context
  }
}