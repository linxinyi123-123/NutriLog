// features/recommendation/ui/screens/RecommendationScreen.kt
package com.example.nutrilog.features.recommendation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrilog.features.recommendation.viewmodel.RecommendationViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
// features/recommendation/ui/screens/RecommendationScreen.kt
@Composable
fun RecommendationScreen() {
    val viewModel: RecommendationViewModel = viewModel()

    // 使用 collectAsState() 收集状态
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val challenges by viewModel.challenges.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 加载状态 - 现在使用收集到的状态
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // 错误提示 - 现在使用收集到的状态
            error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // 推荐列表 - 现在使用收集到的状态
            Text(
                text = "个性化推荐",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(recommendations) { recommendation ->
                    RecommendationCard(
                        recommendation = recommendation,
                        onApply = { viewModel.markRecommendationApplied(recommendation.id) },
                        onDismiss = { /* 稍后处理 */ }
                    )
                }

                if (recommendations.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("暂无推荐")
                        }
                    }
                }
            }

            // 挑战部分 - 现在使用收集到的状态
            Text(
                text = "今日挑战",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(challenges) { challenge ->
                    ChallengeCard(challenge = challenge)
                }
            }
        }
    }
}
@Composable
fun RecommendationCard(
    recommendation: com.example.nutrilog.features.recommendation.model.Recommendation,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recommendation.description,
                style = MaterialTheme.typography.body2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onDismiss) {
                    Text("稍后再说")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onApply) {
                    Text("立即执行")
                }
            }
        }
    }
}

// 修复ChallengeCard中的onUpdateProgress函数
@Composable
fun ChallengeCard(
    challenge: com.example.nutrilog.features.recommendation.challenge.DailyChallenge,
    onUpdateProgress: (Long, Float) -> Unit = { _, _ -> }
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }

                if (challenge.completed) {
                    Text(
                        text = "已完成",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = challenge.progress / challenge.target,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "进度: ${challenge.progress.toInt()}/${challenge.target.toInt()} ${challenge.unit}",
                    style = MaterialTheme.typography.caption
                )

                // 修复：使用challenge.id而不是硬编码
                Button(
                    onClick = {
                        val newProgress = minOf(challenge.progress + 1, challenge.target)
                        onUpdateProgress(challenge.id, newProgress)
                    },
                    enabled = !challenge.completed && challenge.progress < challenge.target
                ) {
                    Text("更新进度")
                }
            }
        }
    }
}