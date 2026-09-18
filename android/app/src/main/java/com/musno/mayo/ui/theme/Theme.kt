package com.musno.mayo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Green700,
    onPrimary = SurfaceLight,
    primaryContainer = Green100,
    onPrimaryContainer = Green800,
    secondary = Green50,
    onSecondary = Green800,
    background = SurfaceLight,
    onBackground = Green800,
    surface = SurfaceLight,
    onSurface = Green800,
    error = ErrorRed,
)

private val DarkColorScheme = darkColorScheme(
    primary = Green100,
    onPrimary = Green800,
    primaryContainer = Green700,
    onPrimaryContainer = Green100,
    secondary = Green50,
    onSecondary = Green700,
    background = Green800,
    onBackground = Green100,
    surface = Green800,
    onSurface = Green100,
)

@Composable
fun MayoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}