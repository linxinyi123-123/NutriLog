package com.example.nutrilog.ui.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import com.example.nutrilog.ThemeState
import com.example.nutrilog.ui.theme.AppColors

@Composable
fun ThemeSwitcher() {
    Switch(
        checked = ThemeState.isDarkTheme.value,
        onCheckedChange = { ThemeState.onThemeChange?.invoke(it) },
        colors = SwitchDefaults.colors(
            checkedThumbColor = AppColors.Primary,
            checkedTrackColor = AppColors.Primary.copy(alpha = 0.5f)
        )
    )
}