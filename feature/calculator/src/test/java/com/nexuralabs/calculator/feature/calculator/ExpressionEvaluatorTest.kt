package com.nexuralabs.calculator.feature.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal

class ExpressionEvaluatorTest {

    @Test
    fun testBasicAddition() {
        val result = ExpressionEvaluator.evaluate("2+3")
        assertEquals(BigDecimal("5"), result)
    }

    @Test
    fun testPrecedence() {
        val result = ExpressionEvaluator.evaluate("2+3*4")
        assertEquals(BigDecimal("14"), result)
    }

    @Test
    fun testUnaryMinus() {
        val result = ExpressionEvaluator.evaluate("-5+3")
        assertEquals(BigDecimal("-2"), result)
    }

    @Test
    fun testSquareRoot() {
        val result = ExpressionEvaluator.evaluate("√16")
        assertEquals(BigDecimal("4"), result)
    }

    @Test
    fun testPercent() {
        val result = ExpressionEvaluator.evaluate("50%")
        assertEquals(BigDecimal("0.5"), result)
    }

    @Test
    fun testExponent() {
        val result = ExpressionEvaluator.evaluate("2^3")
        assertEquals(BigDecimal("8"), result)
    }

    @Test
    fun testDivisionByZeroThrows() {
        assertThrows(ArithmeticException::class.java) {
            ExpressionEvaluator.evaluate("5/0")
        }
    }
}
