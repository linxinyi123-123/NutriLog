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
        elevation = CardDefaults.cardElevation(4.dp),
        shape = AppShapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题
            Text(
                text = "今日摘要",
                style = AppTypography.h2,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 营养数据网格 - 简化实现
            Column {
                // 第一行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoGridItem(
                        title = "卡路里",
                        value = summary.calories.toString(),
                        unit = "kcal",
                        color = AppColors.Primary,
                        modifier = Modifier.weight(1f)
                    )
                    InfoGridItem(
                        title = "蛋白质",
                        value = summary.protein.toString(),
                        unit = "g",
                        color = AppColors.Protein,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 第二行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoGridItem(
                        title = "碳水",
                        value = summary.carbs.toString(),
                        unit = "g",
                        color = AppColors.Carbs,
                        modifier = Modifier.weight(1f)
                    )
                    InfoGridItem(
                        title = "脂肪",
                        value = summary.fat.toString(),
                        unit = "g",
                        color = AppColors.Fat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
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
                    onClick = { navController.navigate("add_record") }
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

// 删除有问题的 GridRow 函数