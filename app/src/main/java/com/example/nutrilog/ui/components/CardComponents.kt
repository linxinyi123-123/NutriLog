package com.example.nutrilog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.nutrilog.ui.theme.AppColors
import com.example.nutrilog.ui.theme.AppShapes
import com.example.nutrilog.ui.theme.AppTypography

// 基础卡片组件
@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    elevation: CardElevation = CardDefaults.cardElevation(4.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier.fillMaxWidth().clickable(onClick = onClick)
    } else {
        modifier.fillMaxWidth()
    }
    
    Card(
        modifier = cardModifier,
        elevation = elevation,
        shape = AppShapes.medium
    ) {
        content()
    }
}

// 信息卡片组件
@Composable
fun InfoCard(
    title: String,
    value: String,
    unit: String = "",
    icon: ImageVector? = null,
    color: Color = AppColors.Primary
) {
    BaseCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = color
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                Text(
                    text = title,
                    style = AppTypography.caption,
                    color = AppColors.OnSurfaceVariant
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = value,
                        style = AppTypography.h2,
                        color = AppColors.OnSurface
                    )
                    if (unit.isNotEmpty()) {
                        Text(
                            text = unit,
                            style = AppTypography.body1,
                            color = AppColors.OnSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// 健康评分徽章组件
@Composable
fun HealthScoreBadge(score: Int) {
    val badgeColor = when {
        score >= 80 -> AppColors.Success
        score >= 60 -> AppColors.Warning
        else -> AppColors.Error
    }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(color = badgeColor, shape = AppShapes.extraLarge)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "健康分: $score",
            style = AppTypography.body1,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}