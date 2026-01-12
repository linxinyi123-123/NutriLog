package com.example.nutrilog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.nutrilog.ui.theme.AppColors
import com.example.nutrilog.ui.theme.AppTypography

// 演示模式开关组件
@Composable
fun DemoModeToggle(
    isDemoMode: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Switch(
        checked = isDemoMode,
        onCheckedChange = onToggle,
        colors = SwitchDefaults.colors(
            checkedThumbColor = AppColors.Primary,
            checkedTrackColor = AppColors.Primary.copy(alpha = 0.5f)
        ),
        modifier = Modifier.semantics {
            contentDescription = "切换演示模式"
        }
    )
}

// 演示数据提供器组件
@Composable
inline fun <reified T> DemoDataProvider(
    isDemoMode: Boolean,
    realDataProvider: @Composable () -> T,
    demoDataProvider: @Composable () -> T
): T {
    return if (isDemoMode) {
        DemoModeIndicator()
        demoDataProvider()
    } else {
        realDataProvider()
    }
}

// 演示模式指示器
@Composable
fun DemoModeIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.01f))
    ) {
        Text(
            text = "演示模式",
            style = AppTypography.caption.copy(color = AppColors.Primary),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color.White, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
