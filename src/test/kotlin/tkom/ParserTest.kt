package tkom

import org.junit.Test
import tkom.lexer.Lexer
import tkom.parser.Parser
import tkom.source.CommandLineSource
import kotlin.test.assertEquals

class ParserTest {

  @Test
  fun test1() {
    val testProgram = "a = 5\n"
    val source = CommandLineSource(testProgram.byteInputStream())
//    val source = CommandLineSource(System.`in`)
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse())
  }

  @Test
  fun test2() {
    val testProgram = "for (i = 0; i < 5; i = i + 1) { \na = a - 5 \n}\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse())
  }

  @Test
  fun test3() {
    val testProgram = "if (a == 0) {\n x * 5 \n}\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse())
  }

  @Test
  fun test4() {
    val testProgram = "fun a(b, c) { \nx = b * c; return x; }\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse())
  }

  @Test
  fun test5() {
    val testProgram = "fun a() {\n x = b * c; return x; }\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse())
  }

  @Test
  fun test6() {
    val testProgram = "fun a() {\n x b * c; return x; }\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(1, parser.parse())
  }

  @Test
  fun test7() {
    val testProgram = "a()\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse())
  }

  @Test
  fun test8() {
    val testProgram = "a()\nb = 5\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse())
    assertEquals(0, parser.parse())
  }
}