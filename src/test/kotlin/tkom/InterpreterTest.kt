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

}