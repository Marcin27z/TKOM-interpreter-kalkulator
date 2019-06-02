package tkom

import org.junit.Test
import tkom.interpreter.Interpreter
import tkom.source.CommandLineSource
import kotlin.test.assertEquals
import java.io.ByteArrayOutputStream
import java.io.PrintStream


class InterpreterTest {

  @Test
  fun assignmentTest() {
    val testProgram = "a = 5\n a\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    val resultNumber = interpreter.singleTestRun()
    assertEquals(5.0, resultNumber.realPart)
  }

  @Test
  fun functionReturnTest() {
    val function =
        "fun sgn(x) {\n" +
            "if (x > 0) {\n" +
            "return 1\n" +
            "} else if (x == 0) {\n" +
            "return 0\n" +
            "} else {\n" +
            "return -1\n" +
            "}\n" +
            "}\n"
    val call1 = "sgn(5)\n"
    val call2 = "sgn(0)\n"
    val call3 = "sgn(-4)\n"
    val source = CommandLineSource((function + call1).byteInputStream())
    val interpreter = Interpreter(source)
    val resultNumber = interpreter.singleTestRun()
    assertEquals(1.0, resultNumber.realPart)
    val source2 = CommandLineSource((function + call2).byteInputStream())
    val interpreter2 = Interpreter(source2)
    val resultNumber2 = interpreter2.singleTestRun()
    assertEquals(0.0, resultNumber2.realPart)
    val source3 = CommandLineSource((function + call3).byteInputStream())
    val interpreter3 = Interpreter(source3)
    val resultNumber3 = interpreter3.singleTestRun()
    assertEquals(-1.0, resultNumber3.realPart)
  }

  @Test
  fun loopBreakTest() {
    val testProgram = "a = 0\n" +
        "for (i = 0; i < 10; i = i + 1) {\n" +
        "a = a + 1\n" +
        "if (a > 5) {\n" +
        "break\n" +
        "}\n" +
        "}\n" +
        "a\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    val resultNumber = interpreter.singleTestRun()
    assertEquals(6.0, resultNumber.realPart)
  }

  @Test
  fun loopContinueTest() {
    val testProgram = "a = 0\n" +
        "for (i = 0; i < 10; i = i + 1) {\n" +
        "a = a + 1\n" +
        "if (a > 5) {\n" +
        "continue\n" +
        "}\n" +
        "a = a + 1\n" +
        "}\n" +
        "a\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    val resultNumber = interpreter.singleTestRun()
    assertEquals(13.0, resultNumber.realPart)
  }

  @Test
  fun arithmeticExpressionTest() {
    val testProgram = "((5 + 3) * (4 + 4)) / 16\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    val resultNumber = interpreter.singleTestRun()
    assertEquals(4.0, resultNumber.realPart)
  }

  @Test
  fun recurencyTest() {
    val testProgram = "fun silnia(x) {\n" +
        "if (x == 1) {\n" +
        "return 1\n" +
        "}\n" +
        "return x * silnia(x - 1)\n" +
        "}\n" +
        "silnia(4)\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    val resultNumber = interpreter.singleTestRun()
    assertEquals(24.0, resultNumber.realPart)
  }

  @Test
  fun printTest() {
    val originalOut = System.out
    val outContent = ByteArrayOutputStream()
    System.setOut(PrintStream(outContent))
    val testProgram = "a = 5\n print(a)\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    interpreter.singleTestRun()
    assertEquals("5.0", outContent.toString())
    System.setOut(originalOut)
  }

  @Test
  fun notInitializedVariableTest() {
    val originalOut = System.out
    val outContent = ByteArrayOutputStream()
    System.setOut(PrintStream(outContent))
    val testProgram = "a\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    interpreter.singleTestRun()
    assertEquals("Variable a is not initialized\r\n", outContent.toString())
    System.setOut(originalOut)
  }

  @Test
  fun notDefinedFunctionVariableTest() {
    val originalOut = System.out
    val outContent = ByteArrayOutputStream()
    System.setOut(PrintStream(outContent))
    val testProgram = "a()\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    interpreter.singleTestRun()
    assertEquals("No function with name \"a\" and 0 arguments defined\r\n", outContent.toString())
    System.setOut(originalOut)
  }

  @Test
  fun wrongNumberOfArgumentsForFunction() {
    val originalOut = System.out
    val outContent = ByteArrayOutputStream()
    System.setOut(PrintStream(outContent))
    val testProgram = "fun a(){\n" +
        "}\n" +
        "a(1)\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    interpreter.singleTestRun()
    assertEquals("No function with name \"a\" and 1 arguments defined\r\n", outContent.toString())
    System.setOut(originalOut)
  }

  @Test
  fun unexpectedBreak() {
    val originalOut = System.out
    val outContent = ByteArrayOutputStream()
    System.setOut(PrintStream(outContent))
    val testProgram = "break\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    interpreter.singleTestRun()
    assertEquals("Unexpected break statement\r\n", outContent.toString())
    System.setOut(originalOut)
  }

  @Test
  fun unexpectedReturn() {
    val originalOut = System.out
    val outContent = ByteArrayOutputStream()
    System.setOut(PrintStream(outContent))
    val testProgram = "return 5\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    interpreter.singleTestRun()
    assertEquals("Unexpected return statement\r\n", outContent.toString())
    System.setOut(originalOut)
  }

  @Test
  fun parseErrorTest1() {
    val originalOut = System.out
    val outContent = ByteArrayOutputStream()
    System.setOut(PrintStream(outContent))
    val testProgram = "a = 5;\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    interpreter.singleTestRun()
    assertEquals("a = 5;\r\n" +
        "     ^\r\n" +
        "line: 0, column: 6 expected LINE_BREAK\r\n", outContent.toString())
    System.setOut(originalOut)
  }

  @Test
  fun parseErrorTest2() {
    val originalOut = System.out
    val outContent = ByteArrayOutputStream()
    System.setOut(PrintStream(outContent))
    val testProgram = "a = 5,5\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    interpreter.singleTestRun()
    assertEquals("a = 5,5\r\n" +
        "     ^\r\n" +
        "line: 0, column: 6 expected LINE_BREAK\r\n" +
        "a = 5,5\r\n" +
        "     ^\r\n" +
        "line: 0, column: 6 unexpected token: ,\r\n" +
        "a = 5,5\r\n" +
        "      ^\r\n" +
        "line: 0, column: 7 expected LINE_BREAK\r\n", outContent.toString())
    System.setOut(originalOut)
  }

  @Test
  fun parseErrorTest3() {
    val originalOut = System.out
    val outContent = ByteArrayOutputStream()
    System.setOut(PrintStream(outContent))
    val testProgram = "for (j = 0, j < 5, j = j + 1) {\n" +
        "}\n"
    val source = CommandLineSource(testProgram.byteInputStream())
    val interpreter = Interpreter(source)
    interpreter.singleTestRun()
    assertEquals("for (j = 0, j < 5, j = j + 1) {\r\n" +
        "          ^\r\n" +
        "line: 0, column: 11 expected SEMICOLON\r\n" +
        "for (j = 0, j < 5, j = j + 1) {\r\n" +
        "                 ^\r\n" +
        "line: 0, column: 18 expected SEMICOLON\r\n", outContent.toString())
    System.setOut(originalOut)
  }

}