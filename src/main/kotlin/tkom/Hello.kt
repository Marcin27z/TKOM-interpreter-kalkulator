package tkom

import tkom.interpreter.Interpreter
import tkom.source.CommandLineSource
import tkom.source.FileSource
import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>) {
  try {
    val source = if (args.isNotEmpty()) {
      FileSource(FileInputStream(File(args[0])))
    } else {
      CommandLineSource(System.`in`)
    }
    val interpreter = Interpreter(source)
    interpreter.run()
  } catch (e: Exception) {
    println(e)
  }
}