package tkom

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
  val lexer = Lexer(source)
//  do {
//    val token = lexer.getToken()
//    println(token.toString())
//  } while (token.tokenType != TokenType.EOT)
  val parser = Parser(lexer, source)
  var (errors, exitFlag) = parser.parse()
  while (!exitFlag) {
    val pair = parser.parse()
    errors = pair.first
    exitFlag = pair.second
  }
}