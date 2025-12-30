package com.example.nutrilog.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun ResponsiveLayout(
    windowSize: WindowSizeClass,
    content: @Composable (WindowSizeClass) -> Unit
) {
    content(windowSize)
}

@Composable
fun calculateWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> WindowSizeClass.Compact
        screenWidth < 840.dp -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }
}

enum class WindowSizeClass {
    Compact,    // 手机竖屏
    Medium,     // 平板竖屏/手机横屏
    Expanded    // 平板横屏
}
