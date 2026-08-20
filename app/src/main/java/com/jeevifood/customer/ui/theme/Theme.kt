package com.jeevifood.customer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = JeeviRed,
    onPrimary = Color.White,
    secondary = JeeviGreen,
    background = JeeviCream,
    surface = JeeviSurface,
)

private val DarkColors = darkColorScheme(
    primary = JeeviRed,
    secondary = JeeviGreen,
)

@Composable
fun JeeviFoodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
