// app/src/main/java/com/nutrilog/features/recommendation/notification/NotificationService.kt
package com.example.nutrilog.features.recommendation.notification

import com.example.nutrilog.features.recommendation.model.gamification.Achievement
import com.example.nutrilog.features.recommendation.gamification.SpecialReward

/**
 * 通知服务接口（模拟实现）
 */
interface NotificationService {
    suspend fun sendAchievementUnlocked(userId: Long, achievement: Achievement, points: Int)
    suspend fun sendLevelUpNotification(userId: Long, oldLevel: Int, newLevel: Int)
    suspend fun sendRewardNotification(userId: Long, reward: SpecialReward)
    suspend fun sendChallengeCompleted(userId: Long, challengeTitle: String, points: Int)
}