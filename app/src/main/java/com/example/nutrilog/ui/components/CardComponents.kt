package com.example.nutrilog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.nutrilog.ui.theme.AppColors
import com.example.nutrilog.ui.theme.AppShapes
import com.example.nutrilog.ui.theme.AppTypography
import kotlinx.coroutines.delay

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

// 加载动画组件
@Composable
fun LoadingView(
    message: String = "加载中..."
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 旋转动画
        val rotationState = remember { mutableStateOf(0f) }
        
        LaunchedEffect(Unit) {
            var currentRotation = 0f
            while (true) {
                currentRotation += 10f
                if (currentRotation >= 360f) currentRotation = 0f
                delay(16) // 约60fps
            }
        }
        
        Box(
            modifier = Modifier
                .size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = AppColors.Primary,
                strokeWidth = 4.dp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = AppTypography.body1,
            color = AppColors.OnSurfaceVariant
        )
    }
}

// 数据刷新动画组件
@OptIn(ExperimentalMaterialApi::class)
@Composable 
fun RefreshableContent( 
    isRefreshing: Boolean, 
    onRefresh: () -> Unit, 
    content: @Composable () -> Unit 
) { 
    val refreshState = rememberPullRefreshState( 
        refreshing = isRefreshing, 
        onRefresh = onRefresh 
    ) 
    
    Box( 
        modifier = Modifier 
            .fillMaxSize() 
            .pullRefresh(refreshState) 
    ) { 
        content() 
        
        PullRefreshIndicator( 
            refreshing = isRefreshing, 
            state = refreshState, 
            modifier = Modifier.align(Alignment.TopCenter) 
        ) 
    } 
}

// 带动画效果的按钮组件
@Composable 
fun AnimatedButton( 
    text: String, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    enabled: Boolean = true 
) { 
    val isPressedState = remember { mutableStateOf(false) }
    
    Button( 
        onClick = onClick, 
        modifier = modifier 
            .scale(if (isPressedState.value) 0.95f else 1f) 
            .pointerInput(Unit) { 
                detectTapGestures( 
                    onPress = { 
                        isPressedState.value = true 
                        tryAwaitRelease() 
                        isPressedState.value = false 
                    } 
                ) 
            }, 
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Primary,
            disabledContainerColor = AppColors.Primary.copy(alpha = 0.5f)
        )
    ) { 
        Text(text = text) 
    } 
}

// 带有涟漪效果的卡片组件
@Composable 
fun RippleCard( 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    content: @Composable () -> Unit 
) { 
    Card( 
        modifier = modifier 
            .clickable( 
                onClick = onClick, 
                indication = rememberRipple(bounded = true), 
                interactionSource = remember { MutableInteractionSource() } 
            ), 
        elevation = CardDefaults.cardElevation(4.dp), 
        shape = AppShapes.medium
    ) { 
        content() 
    } 
}