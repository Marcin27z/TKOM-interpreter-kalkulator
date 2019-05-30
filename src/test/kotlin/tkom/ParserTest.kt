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
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test2() {
    val testProgram = "for (i = 0; i < 5; i = i + 1) { \na = a - 5 \n}\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test3() {
    val testProgram = "if (a == 0) {\n x * 5 \n}\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test4() {
    val testProgram = "fun a(b, c) { \nx = b * c\n return x\n }\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test5() {
    val testProgram = "fun a() {\n x = b * c\n return x\n }\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test6() {
    val testProgram = "fun a() {\n x b * c\n return x\n }\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(1, parser.parse().first)
  }

  @Test
  fun test7() {
    val testProgram = "a()\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test8() {
    val testProgram = "a()\nb = 5\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test9() {
    val testProgram = "3 + 1\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test10() {
    val testProgram = "fun x(y) {\nreturn y * y\n}\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test11() {
    val testProgram = "j = 0\nfor (i = 0; i < 5; i = i + 1) {\nj = j + 5\n}\nj\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
    assertEquals(0, parser.parse().first)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test12() {
    val testProgram = "(1 + 1i) + (2 + 2.5i)\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test13() {
    val testProgram = "16 + 0xA + 07 + 0b1\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test14() {
    val testProgram = "b = 18.33\nc = a + b\nc\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
    assertEquals(0, parser.parse().first)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test15() {
    val testProgram = "((1 + 3) * (8 + 4)) / 16\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test16() {
    val testProgram = "fun silnia(x) {\nif (x == 0) {\nreturn 1\n }\n return x * silnia(x)\n}\nsilnia(5)\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test17() {
    val testProgram = "a = 4\nif (a % 2 == 0) {\nb = 5\n } else { \nb = 6\n }\nb\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
    assertEquals(0, parser.parse().first)
    assertEquals(0, parser.parse().first)
  }

  @Test
  fun test19() {
    val testProgram = "a - -5\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val lexer = Lexer(source)
    val parser = Parser(lexer, source)
    assertEquals(0, parser.parse().first)
  }


}