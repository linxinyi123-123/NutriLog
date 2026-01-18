package com.example.nutrilog.features.recommendation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.nutrilog.features.recommendation.model.gamification.Achievement
// 添加实验性注解
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementGrid(
    achievements: List<Achievement>,
    onAchievementClick: (Achievement) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(achievements) { achievement ->
            AchievementCard(
                achievement = achievement,
                onClick = { onAchievementClick(achievement) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementCard(
    achievement: Achievement,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 成就图标和标记
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getAchievementIcon(achievement.type),
                    contentDescription = achievement.name,
                    modifier = Modifier.size(40.dp),
                    tint = if (achievement.unlockedAt != null)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )

                // 解锁标志
                if (achievement.unlockedAt != null) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "已解锁",
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 成就名称
            Text(
                text = achievement.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // 成就描述
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 积分
            Text(
                text = "${achievement.points} 积分",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementDetailDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = achievement.name) },
        text = {
            Column {
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "类型:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = when(achievement.type) {
                            com.example.nutrilog.features.recommendation.model.gamification.AchievementType.DAILY -> "日常成就"
                            com.example.nutrilog.features.recommendation.model.gamification.AchievementType.MILESTONE -> "里程碑成就"
                            com.example.nutrilog.features.recommendation.model.gamification.AchievementType.SPECIAL -> "特殊成就"
                            com.example.nutrilog.features.recommendation.model.gamification.AchievementType.SECRET -> "隐藏成就"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "积分:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${achievement.points} 积分",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (achievement.unlockedAt != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "已解锁",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { // 改为 TextButton
                Text("确定")
            }
        }
    )
}
private fun getAchievementIcon(type: com.example.nutrilog.features.recommendation.model.gamification.AchievementType): ImageVector {
    return when(type) {
        com.example.nutrilog.features.recommendation.model.gamification.AchievementType.DAILY -> Icons.Filled.Star
        com.example.nutrilog.features.recommendation.model.gamification.AchievementType.MILESTONE -> Icons.Filled.EmojiEvents
        com.example.nutrilog.features.recommendation.model.gamification.AchievementType.SPECIAL -> Icons.Filled.WorkspacePremium
        com.example.nutrilog.features.recommendation.model.gamification.AchievementType.SECRET -> Icons.Filled.VisibilityOff
    }
}