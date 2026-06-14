package de.seb.simplecalculator

sealed interface CalculatorAction {
    data object Clear : CalculatorAction
    data object Backspace : CalculatorAction
    data object Equals : CalculatorAction
    data class Digit(val value: String) : CalculatorAction
    data object Decimal : CalculatorAction
    data class Operation(val value: String) : CalculatorAction
}
