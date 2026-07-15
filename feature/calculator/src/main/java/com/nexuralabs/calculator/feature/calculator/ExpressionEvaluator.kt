package com.nexuralabs.calculator.feature.calculator

import java.math.BigDecimal
import java.math.MathContext

object ExpressionEvaluator {
    private val operatorsSet = setOf("+", "-", "*", "/", "÷", "×", "−", "^")

    fun evaluate(expr: String): BigDecimal {
        val normalized = expr.replace("×", "*").replace("÷", "/").replace("−", "-")
        val tokens = mutableListOf<String>()
        var currentNumber = ""

        for (char in normalized) {
            if (char.isDigit() || char == '.') {
                currentNumber += char
            } else {
                if (currentNumber.isNotEmpty()) tokens.add(currentNumber)
                tokens.add(char.toString())
                currentNumber = ""
            }
        }
        if (currentNumber.isNotEmpty()) tokens.add(currentNumber)

        val output = java.util.ArrayDeque<BigDecimal>()
        val ops = java.util.ArrayDeque<String>()
        val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2, "^" to 3, "u-" to 4, "√" to 4)
        val rightAssociative = setOf("u-", "√")

        var lastWasOperator = true

        for (token in tokens) {
            var mappedToken = token
            if (token == "-" && lastWasOperator) {
                mappedToken = "u-"
            }

            when {
                mappedToken.toBigDecimalOrNull() != null -> {
                    output.addLast(mappedToken.toBigDecimal())
                    lastWasOperator = false
                }
                mappedToken == "%" -> {
                    if (output.isNotEmpty()) {
                        val v = output.removeLast()
                        output.addLast(v.divide(BigDecimal("100"), MathContext.DECIMAL128))
                    }
                    lastWasOperator = false
                }
                precedence.containsKey(mappedToken) -> {
                    val isRight = mappedToken in rightAssociative
                    val currPrec = precedence[mappedToken] ?: 0
                    while (ops.isNotEmpty()) {
                        val top = ops.last()
                        val topPrec = precedence.getOrDefault(top, 0)
                        if (topPrec > currPrec || (!isRight && topPrec == currPrec)) {
                            applyOp(output, ops.removeLast())
                        } else {
                            break
                        }
                    }
                    ops.addLast(mappedToken)
                    lastWasOperator = true
                }
            }
        }
        while (ops.isNotEmpty()) applyOp(output, ops.removeLast())
        return if (output.isEmpty()) BigDecimal.ZERO else output.first()
    }

    private fun applyOp(output: java.util.ArrayDeque<BigDecimal>, op: String) {
        if (op == "√") {
            if (output.isEmpty()) return
            val a = output.removeLast()
            output.addLast(BigDecimal(Math.sqrt(a.toDouble())))
            return
        }
        if (op == "u-") {
            if (output.isEmpty()) return
            val a = output.removeLast()
            output.addLast(a.negate())
            return
        }
        if (output.size < 2) return
        val b = output.removeLast()
        val a = output.removeLast()
        val res = when (op) {
            "+" -> a.add(b)
            "-" -> a.subtract(b)
            "*" -> a.multiply(b)
            "/" -> if (b.compareTo(BigDecimal.ZERO) == 0) throw ArithmeticException("Div zero") else a.divide(b, MathContext.DECIMAL128)
            "^" -> BigDecimal(Math.pow(a.toDouble(), b.toDouble()))
            else -> BigDecimal.ZERO
        }
        output.addLast(res)
    }
}
