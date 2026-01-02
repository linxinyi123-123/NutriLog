// app/src/main/java/com/nutrilog/features/recommendation/gamification/AchievementProgress.kt
package com.example.nutrilog.features.recommendation.gamification

/**
 * 成就进度跟踪
 */
data class AchievementProgress(
    val achievementId: Long,
    val userId: Long,
    val currentValue: Float,      // 当前值
    val targetValue: Float,       // 目标值
    val progress: Float,          // 进度 (0-1)
    val updatedAt: Long = System.currentTimeMillis()
)