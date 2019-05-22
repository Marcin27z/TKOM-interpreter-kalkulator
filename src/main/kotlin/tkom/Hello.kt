package tkom

import tkom.interpreter.Interpreter
import tkom.lexer.Lexer
import tkom.parser.Parser
import tkom.source.CommandLineSource
import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>) {
  val source = if (args.isNotEmpty()) {
    CommandLineSource(FileInputStream(File(args[0])))
  } else {
    CommandLineSource(System.`in`)
  }
  val interpreter = Interpreter(source)
  interpreter.run()
}