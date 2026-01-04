// app/src/main/java/com/nutrilog/features/recommendation/gamification/UserStats.kt
package com.example.nutrilog.features.recommendation.gamification

/**
 * 用户统计数据模型
 */
data class UserStats(
    val userId: Long,
    val totalPoints: Int = 0,
    val level: Int = 0,
    val unlockedAchievements: Int = 0,
    val totalAchievements: Int = 0,
    val achievementCompletionRate: Float = 0f
)