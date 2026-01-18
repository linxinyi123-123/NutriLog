package com.example.nutrilog.features.recommendation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrilog.features.recommendation.ui.components.*
import com.example.nutrilog.features.recommendation.viewmodel.RecommendationViewModel
import com.example.nutrilog.ui.components.HealthScoreBadge

// 添加实验性注解
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationDashboard() {
    val viewModel: RecommendationViewModel = viewModel()

    // 收集状态
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    // 本地状态
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("推荐", "挑战", "成就", "计划")

    var selectedAchievement by remember { mutableStateOf<com.example.nutrilog.features.recommendation.model.gamification.Achievement?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("智能推荐") }
            )
        }
    ) { padding ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 错误提示
                error?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // 1. 健康概览
                HealthOverviewSection()

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Tab布局 - 使用 Column 而不是 TabbedRecommendationContent
                Column(modifier = Modifier.fillMaxWidth()) {
                    // 标签栏
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        edgePadding = 16.dp,
                        containerColor = Color.Transparent,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                height = 3.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        divider = {}
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

                    // 内容区域 - 使用 Box 包裹，确保有固定高度
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // 使用权重确保占满剩余空间
                    ) {
                        when(selectedTab) {
                            0 -> RecommendationTabContent(
                                recommendations = recommendations,
                                onRecommendationClick = { /* 处理推荐点击 */ },
                                onApply = viewModel::markRecommendationApplied,
                                onDismiss = { /* 处理推荐忽略 */ }
                            )
                            1 -> ChallengeTabContent(
                                challenges = challenges,
                                onUpdateProgress = viewModel::updateChallengeProgress
                            )
                            2 -> AchievementTabContent(
                                achievements = achievements,
                                onAchievementClick = { selectedAchievement = it }
                            )
                            3 -> PlanTabContent()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // 成就详情对话框
    selectedAchievement?.let { achievement ->
        AchievementDetailDialog(
            achievement = achievement,
            onDismiss = { selectedAchievement = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthOverviewSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和更多按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "健康概览",
                    style = MaterialTheme.typography.headlineMedium
                )

                // 健康分数
                HealthScoreBadge(score = 72)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 统计数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "7",
                    label = "连续记录",
                    icon = Icons.Filled.Star
                )

                StatItem(
                    value = "4",
                    label = "今日挑战",
                    icon = Icons.Filled.TrendingUp
                )

                StatItem(
                    value = "85%",
                    label = "计划进度",
                    icon = Icons.Filled.TrendingUp
                )
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun RecommendationTabContent(
    recommendations: List<com.example.nutrilog.features.recommendation.model.Recommendation>,
    onRecommendationClick: (com.example.nutrilog.features.recommendation.model.Recommendation) -> Unit,
    onApply: (Long) -> Unit,
    onDismiss: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // 推荐分类标签
        RecommendationTabs(
            recommendations = recommendations,
            onRecommendationClick = onRecommendationClick,
            onApply = onApply,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun ChallengeTabContent(
    challenges: List<com.example.nutrilog.features.recommendation.challenge.DailyChallenge>,
    onUpdateProgress: (Long, Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "今日挑战",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (challenges.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f), // 使用相对高度
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.EmojiEvents,
                        contentDescription = "暂无挑战",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "暂无今日挑战",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            // 使用 Column 而不是 LazyColumn，调整间距与其他卡片保持一致
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()), // 添加垂直滚动
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                challenges.forEach { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        onUpdateProgress = onUpdateProgress
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementTabContent(
    achievements: List<com.example.nutrilog.features.recommendation.model.gamification.Achievement>,
    onAchievementClick: (com.example.nutrilog.features.recommendation.model.gamification.Achievement) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // 成就统计
        val unlockedCount = achievements.count { it.unlockedAt != null }
        val totalCount = achievements.size

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "我的成就",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "$unlockedCount/$totalCount",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 成就导航栏 - 让用户可以切换查看全部、已解锁或未解锁成就
        var selectedCategory by remember { mutableStateOf("全部") }
        val categories = listOf("全部", "已解锁", "待解锁")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                OutlinedButton(
                    onClick = { selectedCategory = category },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (selectedCategory == category) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                ) {
                    Text(text = category)
                }
            }
        }

        // 根据选择过滤成就
        val filteredAchievements = when (selectedCategory) {
            "已解锁" -> achievements.filter { it.unlockedAt != null }
            "待解锁" -> achievements.filter { it.unlockedAt == null }
            else -> achievements
        }

        // 显示过滤后的成就
        if (filteredAchievements.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (selectedCategory) {
                        "已解锁" -> "暂无已解锁成就"
                        "待解锁" -> "暂无待解锁成就"
                        else -> "暂无成就"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            // 使用 Column 显示成就列表
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredAchievements.forEach { achievement ->
                    AchievementItem(
                        achievement = achievement,
                        onClick = onAchievementClick
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementItem(
    achievement: com.example.nutrilog.features.recommendation.model.gamification.Achievement,
    onClick: (com.example.nutrilog.features.recommendation.model.gamification.Achievement) -> Unit
) {
    val isUnlocked = achievement.unlockedAt != null
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onClick(achievement) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 成就图标 - 根据解锁状态改变颜色
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = achievement.name,
                modifier = Modifier.size(40.dp),
                tint = if (isUnlocked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 成就信息
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = achievement.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isUnlocked) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                    
                    // 解锁状态标记
                    if (isUnlocked) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "已解锁",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "未解锁",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Text(
                            text = "${achievement.points} 积分",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isUnlocked) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            },
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        // 解锁时间
                        if (isUnlocked && achievement.unlockedAt != null) {
                            Text(
                                text = "已解锁",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            Text(
                                text = "未解锁",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "改善计划",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 模拟改善计划数据
        val mockPlan = com.example.nutrilog.features.recommendation.mock.EnhancedMockData.generateImprovementPlan(1L)

        ImprovementPlanCard(
            plan = mockPlan,
            onTaskComplete = { taskId ->
                // 处理任务完成
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 计划统计
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "计划统计",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PlanStatItem(value = "65%", label = "总体进度")
                    PlanStatItem(value = "2/4", label = "完成周数")
                    PlanStatItem(value = "8", label = "已完成任务")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 创建新计划按钮
        Button(
            onClick = { /* 创建新计划 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = "添加")
            Spacer(modifier = Modifier.width(8.dp))
            Text("创建新的改善计划")
        }
    }
}

@Composable
fun PlanStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}