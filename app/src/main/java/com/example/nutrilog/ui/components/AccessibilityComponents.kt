package com.example.nutrilog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.nutrilog.ui.theme.AppColors

// 无障碍按钮组件
@Composable
fun AccessibleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String = text
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = text, modifier = Modifier.semantics { 
            contentDescription = description
            role = Role.Button
        })
    }
}

// 无障碍图片组件
@Composable
fun AccessibleImage(
    painter: androidx.compose.ui.graphics.painter.Painter?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: androidx.compose.ui.layout.ContentScale = androidx.compose.ui.layout.ContentScale.Crop
) {
    if (painter != null) {
        androidx.compose.foundation.Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        // 占位符或错误状态
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.Background),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = "图片加载失败",
                tint = AppColors.OnSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

// 高对比度模式支持
@Composable
fun HighContrastMode() {
    val isHighContrast = androidx.compose.ui.platform.LocalConfiguration.current.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES

    if (isHighContrast) {
        androidx.compose.runtime.DisposableEffect(Unit) {
            // 可以在这里添加高对比度模式的额外支持
            // 例如调整某些组件的样式
            onDispose {
                // 清理资源
            }
        }
    }
}

// 无障碍文本组件
@Composable
fun AccessibleText(
    text: String,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Text(
        text = text,
        modifier = if (description != null) {
            modifier.semantics { 
                contentDescription = description
            }
        } else {
            modifier
        },
        style = MaterialTheme.typography.bodyLarge
    )
}

// 无障碍加载指示器
@Composable
fun AccessibleCircularProgressIndicator(
    modifier: Modifier = Modifier,
    description: String = "加载中"
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = AppColors.Primary,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}
