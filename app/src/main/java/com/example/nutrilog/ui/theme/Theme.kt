package com.example.nutrilog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = AppColors.PrimaryVariant,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,
    secondary = AppColors.Secondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = AppColors.SecondaryVariant,
    onSecondaryContainer = androidx.compose.ui.graphics.Color.White,
    background = AppColors.Background,
    onBackground = AppColors.OnSurface,
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.Background,
    onSurfaceVariant = AppColors.OnSurfaceVariant,
    error = AppColors.Error
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = AppColors.PrimaryVariant,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.Black,
    secondary = AppColors.Secondary,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    secondaryContainer = AppColors.SecondaryVariant,
    onSecondaryContainer = androidx.compose.ui.graphics.Color.Black,
    background = AppColors.OnSurface,
    onBackground = AppColors.Background,
    surface = AppColors.OnSurfaceVariant,
    onSurface = AppColors.Background,
    surfaceVariant = AppColors.OnSurfaceVariant,
    onSurfaceVariant = AppColors.Background,
    error = AppColors.Error
)

@Composable
fun NutriLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(
            headlineLarge = AppTypography.h1,
            headlineMedium = AppTypography.h2,
            bodyLarge = AppTypography.body1,
            bodyMedium = AppTypography.body1,
            labelSmall = AppTypography.caption
        ),
        content = content
    )
}