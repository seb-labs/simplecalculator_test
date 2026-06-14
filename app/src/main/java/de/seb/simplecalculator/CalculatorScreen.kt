package de.seb.simplecalculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Simple Calculator") },
                actions = {
                    IconButton(onClick = { onAction(CalculatorAction.Backspace) }) {
                        Icon(Icons.Default.Backspace, contentDescription = "Backspace")
                    }
                    IconButton(onClick = { onAction(CalculatorAction.Clear) }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets.navigationBars.only(WindowInsetsSides.Bottom),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            DisplayCard(
                expression = state.expression,
                display = state.display,
                error = state.error,
            )
            Spacer(modifier = Modifier.height(16.dp))
            CalculatorPad(onAction = onAction)
        }
    }
}

@Composable
private fun DisplayCard(
    expression: String,
    display: String,
    error: Boolean,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 24.dp, vertical = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = if (expression.isBlank()) " " else expression,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = display,
                    style = MaterialTheme.typography.displaySmall,
                    color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@Composable
private fun CalculatorPad(onAction: (CalculatorAction) -> Unit) {
    val rows = listOf(
        listOf(ButtonSpec("7"), ButtonSpec("8"), ButtonSpec("9"), ButtonSpec("÷", kind = ButtonKind.Operator, actionLabel = "/")),
        listOf(ButtonSpec("4"), ButtonSpec("5"), ButtonSpec("6"), ButtonSpec("×", kind = ButtonKind.Operator, actionLabel = "*")),
        listOf(ButtonSpec("1"), ButtonSpec("2"), ButtonSpec("3"), ButtonSpec("−", kind = ButtonKind.Operator, actionLabel = "-")),
        listOf(ButtonSpec("0", span = 2), ButtonSpec(".", kind = ButtonKind.Utility), ButtonSpec("+", kind = ButtonKind.Operator)),
        listOf(ButtonSpec("C", kind = ButtonKind.Utility), ButtonSpec("⌫", kind = ButtonKind.Utility), ButtonSpec("=", kind = ButtonKind.Equal)),
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { spec ->
                    val weight = if (spec.span == 2) 2f else 1f
                    CalculatorButton(
                        spec = spec,
                        modifier = Modifier
                            .weight(weight)
                            .height(72.dp),
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

private enum class ButtonKind { Digit, Operator, Utility, Equal }

private data class ButtonSpec(
    val label: String,
    val kind: ButtonKind = ButtonKind.Digit,
    val span: Int = 1,
    val actionLabel: String = label,
)

@Composable
private fun CalculatorButton(
    spec: ButtonSpec,
    modifier: Modifier,
    onAction: (CalculatorAction) -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    val colors = when (spec.kind) {
        ButtonKind.Digit -> ButtonDefaults.filledTonalButtonColors()
        ButtonKind.Utility -> ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ButtonKind.Operator -> ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        ButtonKind.Equal -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    }

    if (spec.kind == ButtonKind.Equal) {
        Button(
            onClick = { onAction(CalculatorAction.Equals) },
            modifier = modifier,
            shape = shape,
            colors = colors,
        ) {
            Text(spec.label, style = MaterialTheme.typography.titleLarge)
        }
        return
    }

    Button(
        onClick = {
            when (spec.actionLabel) {
                "C" -> onAction(CalculatorAction.Clear)
                "⌫" -> onAction(CalculatorAction.Backspace)
                "+", "-", "*", "/" -> onAction(CalculatorAction.Operation(spec.actionLabel))
                "." -> onAction(CalculatorAction.Decimal)
                else -> onAction(CalculatorAction.Digit(spec.actionLabel))
            }
        },
        modifier = modifier,
        shape = shape,
        colors = colors,
    ) {
        Text(spec.label, style = MaterialTheme.typography.titleLarge)
    }
}
