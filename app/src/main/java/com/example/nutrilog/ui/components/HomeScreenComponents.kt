package com.example.nutrilog.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.nutrilog.ui.viewmodels.HealthTrend
import com.example.nutrilog.ui.viewmodels.Meal
import com.example.nutrilog.ui.viewmodels.TodaySummary
import com.example.nutrilog.ui.viewmodels.User
import com.example.nutrilog.ui.theme.AppColors
import com.example.nutrilog.ui.theme.AppShapes
import com.example.nutrilog.ui.theme.AppTypography
import androidx.navigation.NavController

// 欢迎区域组件
@Composable
fun WelcomeSection(user: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = AppShapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像/图标
            Icon(
                Icons.Default.Person,
                contentDescription = "用户头像",
                modifier = Modifier.size(48.dp),
                tint = AppColors.Primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "你好, ${user?.name ?: "用户"}!",
                    style = AppTypography.h1,
                    color = AppColors.OnSurface
                )

                Text(
                    text = "今天是你的第${user?.streakDays ?: 0}天健康之旅",
                    style = AppTypography.body1,
                    color = AppColors.OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 健康评分徽章组件
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = when {
                            (user?.todayScore ?: 0) >= 80 -> AppColors.Success
                            (user?.todayScore ?: 0) >= 60 -> AppColors.Warning
                            else -> AppColors.Error
                        },
                        shape = AppShapes.extraLarge
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "健康分: ${user?.todayScore ?: 0}",
                    style = AppTypography.body1,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// 获取今日日期字符串
fun getTodayDate(): String {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return formatter.format(java.util.Date())
}

// 今日摘要卡片组件
@Composable
fun TodaySummaryCard(
    summary: TodaySummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = AppShapes.medium
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 标题和日期
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "今日摘要",
                    style = AppTypography.h2,
                    color = AppColors.OnSurface
                )
                
                Text(
                    text = getTodayDate(),
                    style = AppTypography.caption,
                    color = AppColors.OnSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 营养数据网格
            NutritionGrid(summary)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 健康评分进度条
            HealthScoreProgress(score = 85) // 临时使用固定分数，实际应从 summary 中获取
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 快速提示
            Text(
                text = "今日饮食搭配均衡，继续保持！",
                style = AppTypography.caption,
                color = AppColors.Info,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// 快速操作按钮组件
@Composable
fun QuickActionsRow(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = AppShapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "快速操作",
                style = AppTypography.h2,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.AddCircle,
                    label = "添加记录",
                    onClick = { /* 实现添加记录逻辑 */ }
                )
                QuickActionButton(
                    icon = Icons.Outlined.Assessment,
                    label = "查看趋势",
                    onClick = { /* 实现查看趋势逻辑 */ }
                )
                QuickActionButton(
                    icon = Icons.Outlined.CalendarToday,
                    label = "日历",
                    onClick = { /* 实现日历逻辑 */ }
                )
                QuickActionButton(
                    icon = Icons.Outlined.Description,
                    label = "营养建议",
                    onClick = { /* 实现营养建议逻辑 */ }
                )
            }
        }
    }
}

// 健康趋势卡片组件
@Composable
fun HealthTrendCard(trend: HealthTrend) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = AppShapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "健康趋势",
                style = AppTypography.h2,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 简化的趋势图表（使用文本展示）
            Column {
                Text(
                    text = "过去7天的营养摄入趋势",
                    style = AppTypography.body1,
                    color = AppColors.OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "卡路里: ${trend.calories.average().toInt()} kcal/天",
                    style = AppTypography.body1,
                    color = AppColors.Primary
                )
                Text(
                    text = "蛋白质: ${trend.protein.average().toInt()} g/天",
                    style = AppTypography.body1,
                    color = AppColors.Protein
                )
                Text(
                    text = "碳水: ${trend.carbs.average().toInt()} g/天",
                    style = AppTypography.body1,
                    color = AppColors.Carbs
                )
                Text(
                    text = "脂肪: ${trend.fat.average().toInt()} g/天",
                    style = AppTypography.body1,
                    color = AppColors.Fat
                )
            }
        }
    }
}

// 最近饮食记录组件
@Composable
fun RecentMealsSection(meals: List<Meal>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = AppShapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "最近饮食记录",
                style = AppTypography.h2,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            meals.forEachIndexed { index, meal: Meal ->
                MealItem(meal = meal)
                if (index < meals.size - 1) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = AppColors.OnSurfaceVariant.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

// 辅助组件：信息网格项
@Composable
fun InfoGridItem(
    title: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = title,
            style = AppTypography.caption,
            color = AppColors.OnSurfaceVariant
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = AppTypography.h2,
                color = color
            )
            Text(
                text = unit,
                style = AppTypography.body1,
                color = AppColors.OnSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }
    }
}

// 辅助组件：快速操作按钮
@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = AppColors.Primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = AppTypography.caption,
            color = AppColors.OnSurface
        )
    }
}

// 辅助组件：饮食记录项
@Composable
fun MealItem(meal: Meal) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 食物图标或图片
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = AppColors.Primary.copy(alpha = 0.1f), shape = AppShapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = meal.name,
                modifier = Modifier.size(24.dp),
                tint = AppColors.Primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = meal.name,
                style = AppTypography.body1,
                color = AppColors.OnSurface
            )
            Text(
                text = meal.time,
                style = AppTypography.caption,
                color = AppColors.OnSurfaceVariant
            )
        }

        Text(
            text = "${meal.calories} kcal",
            style = AppTypography.h2,
            color = AppColors.Primary
        )
    }
}

// 健康评分进度条组件
@Composable
fun HealthScoreProgress(score: Int) {
    Column {
        // 分数标签
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "健康评分",
                style = AppTypography.body1,
                color = AppColors.OnSurface
            )
            
            Text(
                text = "$score/100",
                style = AppTypography.body1.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                color = getScoreColor(score)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 进度条
        androidx.compose.material3.LinearProgressIndicator(
            progress = score / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = getScoreColor(score),
            trackColor = AppColors.Background
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 评分标签
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("需改善", style = AppTypography.caption, color = AppColors.Error)
            Text("良好", style = AppTypography.caption, color = AppColors.Warning)
            Text("优秀", style = AppTypography.caption, color = AppColors.Success)
        }
    }
}

@Composable
fun getScoreColor(score: Int): androidx.compose.ui.graphics.Color {
    return when {
        score < 60 -> AppColors.Error
        score < 80 -> AppColors.Warning
        else -> AppColors.Success
    }
}

// 营养数据网格组件
@Composable
fun NutritionGrid(summary: TodaySummary) {
    val items = listOf(
        Triple("热量", summary.calories, "kcal"),
        Triple("蛋白质", summary.protein.toInt(), "g"),
        Triple("碳水", summary.carbs.toInt(), "g"),
        Triple("脂肪", summary.fat.toInt(), "g")
    )
    
    GridView(
        columns = 2,
        items = items
    ) { (label, value, unit) ->
        NutritionItem(label, value, unit)
    }
}

// 营养数据项组件
@Composable
fun NutritionItem(label: String, value: Any, unit: String) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = label,
            style = AppTypography.caption,
            color = AppColors.OnSurfaceVariant
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value.toString(),
                style = AppTypography.h2,
                color = AppColors.Primary
            )
            Text(
                text = unit,
                style = AppTypography.body1,
                color = AppColors.OnSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }
    }
}

// 简化的网格布局实现，使用 Column 和 Row 替代 LazyVerticalGrid
@Composable
fun <T> GridView(
    columns: Int,
    items: List<T>,
    content: @Composable (T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val rows = (items.size + columns - 1) / columns
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < items.size) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.TopStart
                        ) {
                            content(items[index])
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// 删除有问题的 GridRow 函数