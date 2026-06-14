package de.seb.simplecalculator

data class CalculatorUiState(
    val expression: String = "",
    val display: String = "0",
    val error: Boolean = false,
)
