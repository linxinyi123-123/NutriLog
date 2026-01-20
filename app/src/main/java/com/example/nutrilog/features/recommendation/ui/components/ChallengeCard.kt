package com.example.nutrilog.features.recommendation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nutrilog.features.recommendation.challenge.DailyChallenge

/**
 * 挑战卡片组件 - 使用统一的Material 3样式
 */
@Composable
fun ChallengeCard(
    challenge: DailyChallenge,
    onUpdateProgress: (Long, Float) -> Unit = { _, _ -> }
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                if (challenge.completed) {
                    Text(
                        text = "已完成",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            val progressRatio = challenge.progress / challenge.target
            LinearProgressIndicator(
                progress = progressRatio,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = when {
                    progressRatio >= 1f -> MaterialTheme.colorScheme.secondary
                    progressRatio >= 0.75f -> MaterialTheme.colorScheme.primary
                    progressRatio >= 0.5f -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "进度: ${challenge.progress.toInt()}/${challenge.target.toInt()} ${challenge.unit}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (!challenge.completed) {
                    Button(
                        onClick = { 
                            val newProgress = minOf(challenge.progress + 1, challenge.target)
                            onUpdateProgress(challenge.id, newProgress)
                        },
                        modifier = Modifier.padding(top = 8.dp),
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Text("更新进度")
                    }
                }
            }
        }
    }
}