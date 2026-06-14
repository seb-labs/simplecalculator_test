package de.seb.simplecalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {
    var state by mutableStateOf(CalculatorUiState())
        private set

    fun onAction(action: CalculatorAction) {
        state = when (action) {
            CalculatorAction.Clear -> CalculatorUiState()
            CalculatorAction.Backspace -> state.backspace()
            CalculatorAction.Equals -> state.calculate()
            CalculatorAction.Decimal -> state.appendDecimal()
            is CalculatorAction.Digit -> state.appendDigit(action.value)
            is CalculatorAction.Operation -> state.appendOperation(action.value)
        }
    }
}

private fun CalculatorUiState.appendDigit(value: String): CalculatorUiState {
    val nextExpression = if (error || expression == "0" && display == "0" && display == "0" && expression.isEmpty()) {
        value
    } else if (display == "0" && expression.isEmpty()) {
        value
    } else if (display != expression && expression.isEmpty()) {
        value
    } else {
        expression + value
    }
    return copy(expression = nextExpression, display = nextExpression, error = false)
}

private fun CalculatorUiState.appendDecimal(): CalculatorUiState {
    val current = if (error) "" else expression
    val segment = current.takeLastWhile { it !in "+-*/" }
    if (segment.contains('.')) return this
    val nextExpression = when {
        current.isEmpty() -> "0."
        current.lastOrNull()?.let { it in charArrayOf('+', '-', '*', '/') } == true -> current + "0."
        else -> current + "."
    }
    return copy(expression = nextExpression, display = nextExpression, error = false)
}

private fun CalculatorUiState.appendOperation(value: String): CalculatorUiState {
    if (error) return this
    if (expression.isEmpty()) {
        return if (value == "-") copy(expression = value, display = value) else this
    }
    val nextExpression = if (expression.last() in charArrayOf('+', '-', '*', '/')) {
        expression.dropLast(1) + value
    } else {
        expression + value
    }
    return copy(expression = nextExpression, display = nextExpression, error = false)
}

private fun CalculatorUiState.backspace(): CalculatorUiState {
    if (error) return CalculatorUiState()
    if (expression.isEmpty()) return this
    val trimmed = expression.dropLast(1)
    return copy(
        expression = trimmed,
        display = if (trimmed.isEmpty()) "0" else trimmed,
    )
}

private fun CalculatorUiState.calculate(): CalculatorUiState {
    if (expression.isBlank()) return this
    val result = evaluateExpression(expression)
    return if (result == null) {
        copy(display = "Error", error = true)
    } else {
        val formatted = formatResult(result)
        copy(expression = formatted, display = formatted, error = false)
    }
}

private fun evaluateExpression(expression: String): Double? {
    val cleaned = expression.replace(" ", "")
    if (cleaned.isBlank()) return null

    val values = ArrayDeque<Double>()
    val ops = ArrayDeque<Char>()
    var index = 0

    fun applyTop() {
        val right = values.removeLastOrNull() ?: throw IllegalStateException()
        val left = values.removeLastOrNull() ?: throw IllegalStateException()
        val op = ops.removeLastOrNull() ?: throw IllegalStateException()
        val result = when (op) {
            '+' -> left + right
            '-' -> left - right
            '*' -> left * right
            '/' -> if (right == 0.0) Double.NaN else left / right
            else -> Double.NaN
        }
        values.addLast(result)
    }

    fun precedence(op: Char) = when (op) {
        '+', '-' -> 1
        '*', '/' -> 2
        else -> 0
    }

    while (index < cleaned.length) {
        val ch = cleaned[index]
        val negativeNumber = ch == '-' && (index == 0 || cleaned[index - 1] in charArrayOf('+', '-', '*', '/'))
        if (ch.isDigit() || ch == '.' || negativeNumber) {
            val start = index
            index++
            while (index < cleaned.length && (cleaned[index].isDigit() || cleaned[index] == '.')) {
                index++
            }
            val number = cleaned.substring(start, index)
            val parsed = number.toDoubleOrNull() ?: return null
            values.addLast(parsed)
            continue
        }
        if (ch in charArrayOf('+', '-', '*', '/')) {
            while (ops.isNotEmpty() && precedence(ops.last()) >= precedence(ch)) {
                if (values.size < 2) return null
                applyTop()
            }
            ops.addLast(ch)
            index++
            continue
        }
        return null
    }

    while (ops.isNotEmpty()) {
        if (values.size < 2) return null
        applyTop()
    }

    return values.singleOrNull()?.takeUnless { it.isNaN() || it.isInfinite() }
}

private fun formatResult(value: Double): String {
    val plain = BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
    return plain.removeSuffix(".0")
}
