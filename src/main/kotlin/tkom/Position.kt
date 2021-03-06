package tkom

class Position(var line: Int = 0, var column: Int = 0, var index: Int = 0) {

  constructor(position: Position) : this(position.line, position.column, position.index)

  fun reset() {
    line = 0
    column = 0
    index = 0
  }

  fun nextLine() {
    line++
    column = 0
  }

  fun nextColumn() {
    column++
  }

  fun nextIndex() {
    index++
  }

  override fun equals(other: Any?): Boolean {
    other as Position
    return other.line == line && other.column == column && other.index == index
  }
}

enum class TokenType {
  OPEN_PARENTHESIS,
  CLOSE_PARENTHESIS,
  OPEN_BRACE,
  CLOSE_BRACE,
  EQUAL_SIGN,
  ADDITIVE_OPERATOR,
  MULTIPLICATIVE_OPERATOR,
  POWER_OPERATOR,
  FOR_KEYWORD,
  IF_KEYWORD,
  ELSE_KEYWORD,
  RETURN_KEYWORD,
  FUN_KEYWORD,
  BREAK_KEYWORD,
  CONTINUE_KEYWORD,
  NUMBER,
  IDENTIFIER,
  NEGATION,
  EMPTY,
  ASSIGNMENT,
  RELATIONAL_OPERATOR,
  EOT,
  LOGICAL_AND,
  LOGICAL_OR,
  COMMA,
  SEMICOLON,
  LINE_BREAK,
  BUILT_IN_FUNCTION
}