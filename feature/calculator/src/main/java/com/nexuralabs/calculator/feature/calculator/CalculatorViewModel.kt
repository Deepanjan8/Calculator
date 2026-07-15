package com.nexuralabs.calculator.feature.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuralabs.calculator.core.data.repository.HistoryRepository
import com.nexuralabs.calculator.core.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

private val operatorsSet = setOf("+", "-", "*", "/", "÷", "×", "−", "^")

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _preview = MutableStateFlow("")
    val preview: StateFlow<String> = _preview.asStateFlow()

    val precision: StateFlow<Int> = preferencesRepository.precision
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

    val hapticEnabled: StateFlow<Boolean> = preferencesRepository.hapticEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun onButtonClick(input: String) {
        viewModelScope.launch {
            val current = _expression.value
            when (input) {
                "⌫", "DEL" -> {
                    if (current.isNotEmpty()) {
                        _expression.update { it.dropLast(1) }
                        updatePreviewInBackground()
                    }
                }
                "C" -> { _expression.update { "" }; _preview.update { "" } }
                "." -> {
                    val lastNumber = current.split(Regex("[+\\-×÷^%√]")).lastOrNull() ?: ""
                    if (!lastNumber.contains(".")) _expression.update { it + "." }
                }
                "=" -> {
                    if (current.isNotEmpty() && current.last().toString() !in operatorsSet && current.last() != '√') {
                        try {
                            val result = evaluateExpression(current)
                            val formatted = formatResult(result)
                            historyRepository.insert(current, formatted)
                            _expression.update { formatted }
                            _preview.update { "" }
                        } catch (e: Exception) { _preview.update { "Error" } }
                    }
                }
                "√" -> {
                    if (current.isEmpty() || current.last() != '√') {
                        _expression.update { it + input }
                    }
                    updatePreviewInBackground()
                }
                "%" -> {
                    if (current.isNotEmpty() && (current.last().isDigit() || current.last() == ')')) {
                        _expression.update { it + input }
                    }
                    updatePreviewInBackground()
                }
                else -> {
                    if (input in operatorsSet) {
                        if (current.isEmpty()) return@launch
                        if (current.last().toString() in operatorsSet) {
                            _expression.update { it.dropLast(1) + input }
                        } else if (current.last() != '√') {
                            _expression.update { it + input }
                        }
                    } else { _expression.update { it + input } }
                    updatePreviewInBackground()
                }
            }
        }
    }

    fun loadFromHistory(expr: String) {
        _expression.value = expr
        updatePreviewInBackground()
    }

    private fun updatePreviewInBackground() {
        viewModelScope.launch(Dispatchers.Default) {
            val expr = _expression.value
            if (expr.isEmpty() || expr.last().toString() in operatorsSet || expr.last() == '√') {
                _preview.update { "" }
                return@launch
            }
            try { _preview.update { formatResult(evaluateExpression(expr)) } } catch (_: Exception) { }
        }
    }

    private fun formatResult(result: BigDecimal): String {
        return result.setScale(precision.value, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
    }

    private fun evaluateExpression(expr: String): BigDecimal {
        return ExpressionEvaluator.evaluate(expr)
    }
}
