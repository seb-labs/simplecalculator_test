package de.seb.simplecalculator.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = CalculatorPrimaryLight,
    secondary = CalculatorAccentLight,
    tertiary = CalculatorAccentLight,
    background = CalculatorBackgroundLight,
    surface = CalculatorSurfaceLight,
    surfaceVariant = CalculatorBackgroundLight,
)

private val DarkColors = darkColorScheme(
    primary = CalculatorPrimaryDark,
    secondary = CalculatorAccentDark,
    tertiary = CalculatorAccentDark,
    background = CalculatorBackgroundDark,
    surface = CalculatorSurfaceDark,
    surfaceVariant = CalculatorSurfaceDark,
)

@Composable
fun SimpleCalculatorTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
