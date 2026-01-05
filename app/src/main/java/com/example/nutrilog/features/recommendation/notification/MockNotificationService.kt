// app/src/main/java/com/nutrilog/features/recommendation/notification/MockNotificationService.kt
package com.example.nutrilog.features.recommendation.notification

import com.example.nutrilog.features.recommendation.model.gamification.Achievement
import com.example.nutrilog.features.recommendation.gamification.SpecialReward

/**
 * 模拟通知服务实现（用于测试和独立开发）
 */
class MockNotificationService : NotificationService {

    override suspend fun sendAchievementUnlocked(userId: Long, achievement: Achievement, points: Int) {
        println("🎉 成就解锁通知:")
        println("   用户ID: $userId")
        println("   成就: ${achievement.name}")
        println("   描述: ${achievement.description}")
        println("   奖励积分: $points")
        println()
    }

    override suspend fun sendLevelUpNotification(userId: Long, oldLevel: Int, newLevel: Int) {
        println("⭐ 等级提升通知:")
        println("   用户ID: $userId")
        println("   等级: $oldLevel → $newLevel")
        println()
    }

    override suspend fun sendRewardNotification(userId: Long, reward: SpecialReward) {
        println("🎁 特殊奖励通知:")
        println("   用户ID: $userId")
        println("   奖励类型: ${reward.type}")
        println("   奖励内容: ${reward.description}")
        println()
    }

    override suspend fun sendChallengeCompleted(userId: Long, challengeTitle: String, points: Int) {
        println("✅ 挑战完成通知:")
        println("   用户ID: $userId")
        println("   挑战: $challengeTitle")
        println("   奖励积分: $points")
        println()
    }
}