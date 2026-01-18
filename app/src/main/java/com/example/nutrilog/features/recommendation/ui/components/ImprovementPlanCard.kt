package com.example.nutrilog.features.recommendation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nutrilog.features.recommendation.model.improvement.ImprovementPlan
import com.example.nutrilog.features.recommendation.model.improvement.PlanStatus
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovementPlanCard(
    plan: ImprovementPlan,
    onClick: () -> Unit = {},
    onTaskComplete: (String) -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when(plan.status) {
                PlanStatus.ACTIVE -> MaterialTheme.colorScheme.surfaceVariant
                PlanStatus.COMPLETED -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "第${plan.currentWeek}周 / 共${plan.totalWeeks}周",
                        style = MaterialTheme.typography.labelSmall, // 改为 labelSmall
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // 状态标签
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when(plan.status) {
                        PlanStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                        PlanStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = when(plan.status) {
                            PlanStatus.ACTIVE -> "进行中"
                            PlanStatus.COMPLETED -> "已完成"
                            else -> "已暂停"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 进度条
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "总体进度",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${(plan.progress * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                LinearProgressIndicator(
                    progress = plan.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 本周重点
            Text(
                text = "本周重点: ${plan.weeklyPlans.getOrNull(plan.currentWeek - 1)?.focus ?: ""}",
                style = MaterialTheme.typography.bodyMedium
            )

            // 本周任务
            val weeklyPlan = plan.weeklyPlans.getOrNull(plan.currentWeek - 1)
            weeklyPlan?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "今日任务:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                it.dailyTasks.forEach { task ->
                    DailyTaskItem(
                        task = task,
                        onComplete = { onTaskComplete(task.id) },
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 日期信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "开始: ${plan.startDate}",
                    style = MaterialTheme.typography.labelSmall, // 改为 labelSmall
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "结束: ${plan.endDate}",
                    style = MaterialTheme.typography.labelSmall, // 改为 labelSmall
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTaskItem(
    task: com.example.nutrilog.features.recommendation.model.improvement.DailyTask,
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = false, // 这里需要从数据中获取完成状态
            onCheckedChange = { if (it) onComplete() },
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall, // 改为 bodySmall
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}