package com.clement.zcrypt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


/*
private val DarkColorPalette = darkColors(
    primary = BluePrimary,
    primaryVariant = BlueDark,
    secondary = GreenPrimary,
    secondaryVariant = GreenDark,

    background = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onSurface = Color.White,
    onError = Color.Black,
    error = RedDark,
)

private val LightColorPalette = lightColors(
    primary = BluePrimary,
    primaryVariant = BlueLight,
    secondary = GreenPrimary,
    secondaryVariant = GreenLight,

    background = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onSurface = Color.Black,
    error = RedLight,
    onError = Color.White
)

 */

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200
)

@Composable
fun ZCryptTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}