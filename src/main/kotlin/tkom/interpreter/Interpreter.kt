package tkom.interpreter

import tkom.lexer.Lexer
import tkom.parser.Parser
import tkom.source.CommandLineSource
import tkom.source.Source

class Interpreter(source: Source) {

  private val lexer: Lexer = Lexer(source)
  private val parser: Parser

  init {
    parser = Parser(lexer, source)

  }


}