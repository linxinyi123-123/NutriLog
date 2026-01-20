package com.example.nutrilog.features.recommendation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.nutrilog.features.recommendation.model.Recommendation
import com.example.nutrilog.features.recommendation.model.RecommendationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationTabs(
    recommendations: List<Recommendation>,
    onRecommendationClick: (Recommendation) -> Unit = {},
    onApply: (Long) -> Unit = {},
    onDismiss: (Long) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("全部", "营养缺口", "健康目标", "场景推荐", "习惯改善")

    Column(modifier = Modifier.fillMaxWidth()) {
        // 标签栏
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 内容区域
        RecommendationContent(
            selectedTab = selectedTab,
            recommendations = recommendations,
            onRecommendationClick = onRecommendationClick,
            onApply = onApply,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationContent(
    selectedTab: Int,
    recommendations: List<Recommendation>,
    onRecommendationClick: (Recommendation) -> Unit,
    onApply: (Long) -> Unit,
    onDismiss: (Long) -> Unit
) {
    val filteredRecommendations = when(selectedTab) {
        0 -> recommendations // 全部
        1 -> recommendations.filter { it.type == RecommendationType.NUTRITION_GAP }
        2 -> recommendations.filter { it.type == RecommendationType.MEAL_PLAN }
        3 -> recommendations.filter { it.type == RecommendationType.FOOD_SUGGESTION }
        4 -> recommendations.filter { it.type == RecommendationType.HABIT_IMPROVEMENT }
        else -> recommendations
    }

    if (filteredRecommendations.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.ThumbUp,
                    contentDescription = "暂无推荐",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = when(selectedTab) {
                        0 -> "暂无个性化推荐"
                        1 -> "暂无营养缺口推荐"
                        2 -> "暂无目标相关推荐"
                        3 -> "暂无场景化推荐"
                        4 -> "暂无习惯改善推荐"
                        else -> "暂无推荐"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredRecommendations) { recommendation ->
                EnhancedRecommendationCard(
                    recommendation = recommendation,
                    onClick = { onRecommendationClick(recommendation) },
                    onApply = { onApply(recommendation.id) },
                    onDismiss = { onDismiss(recommendation.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedRecommendationCard(
    recommendation: Recommendation,
    onClick: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和类型标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recommendation.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 类型标签
                    val priorityColor = getPriorityColor(recommendation.priority)
                    Surface(
                        shape = CircleShape,
                        color = priorityColor.copy(alpha = 0.1f),
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text(
                            text = getRecommendationTypeLabel(recommendation.type),
                            style = MaterialTheme.typography.labelSmall,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // 优先级指示器
                PriorityIndicator(
                    priority = recommendation.priority,
                    confidence = recommendation.confidence
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 描述
            Text(
                text = recommendation.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 推荐理由 - 修复 caption 引用
            Text(
                text = "依据：${recommendation.reason}",
                style = MaterialTheme.typography.bodySmall, // 改为 bodySmall
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("稍后再说")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onApply
                ) {
                    Text("立即执行")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityIndicator(
    priority: com.example.nutrilog.features.recommendation.model.Priority,
    confidence: Float
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 优先级标签
        val priorityColor = getPriorityColor(priority)
        Surface(
            shape = CircleShape,
            color = priorityColor
        ) {
            Text(
                text = when(priority) {
                    com.example.nutrilog.features.recommendation.model.Priority.HIGH -> "高"
                    com.example.nutrilog.features.recommendation.model.Priority.MEDIUM -> "中"
                    com.example.nutrilog.features.recommendation.model.Priority.LOW -> "低"
                },
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 置信度
        Text(
            text = "${(confidence * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun getPriorityColor(priority: com.example.nutrilog.features.recommendation.model.Priority): Color {
    return when(priority) {
        com.example.nutrilog.features.recommendation.model.Priority.HIGH -> MaterialTheme.colorScheme.error
        com.example.nutrilog.features.recommendation.model.Priority.MEDIUM -> MaterialTheme.colorScheme.primary
        com.example.nutrilog.features.recommendation.model.Priority.LOW -> MaterialTheme.colorScheme.secondary
    }
}

private fun getRecommendationTypeLabel(type: RecommendationType): String {
    return when(type) {
        RecommendationType.NUTRITION_GAP -> "营养缺口"
        RecommendationType.MEAL_PLAN -> "饮食计划"
        RecommendationType.FOOD_SUGGESTION -> "食物推荐"
        RecommendationType.HABIT_IMPROVEMENT -> "习惯改进"
        RecommendationType.EDUCATIONAL -> "知识教育"
    }
}