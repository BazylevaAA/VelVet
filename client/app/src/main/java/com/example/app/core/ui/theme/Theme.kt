package com.example.app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val VelvetColorScheme = darkColorScheme(
    primary         = VelvetGreen,
    onPrimary       = VelvetBlack,
    secondary       = VelvetGreenDark,
    onSecondary     = VelvetWhite,
    background      = VelvetBlack,
    onBackground    = VelvetWhite,
    surface         = VelvetSurface,
    onSurface       = VelvetWhite,
    surfaceVariant  = VelvetSurfaceHigh,
    onSurfaceVariant = VelvetGray,
    error           = VelvetError,
    onError         = VelvetWhite,
    outline         = VelvetDivider
)

@Composable
fun VelvetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VelvetColorScheme,
        typography  = VelvetTypography,
        content     = content
    )
}