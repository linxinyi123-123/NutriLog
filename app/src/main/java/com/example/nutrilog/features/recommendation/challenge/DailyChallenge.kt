// app/src/main/java/com/nutrilog/features/recommendation/challenge/DailyChallenge.kt
package com.example.nutrilog.features.recommendation.challenge

import java.time.LocalDate

/**
 * 每日挑战数据模型
 */
data class DailyChallenge(
    val id: Long,
    val userId: Long,
    val date: String,              // 挑战日期，格式：2024-01-15
    val title: String,             // 挑战标题
    val description: String,       // 挑战描述
    val type: ChallengeType,       // 挑战类型
    val rewardPoints: Int,         // 奖励积分
    val difficulty: ChallengeDifficulty, // 挑战难度
    val progress: Float,           // 当前进度 (0-1)
    val target: Float,             // 目标值
    val unit: String,              // 单位（如g、次、种）
    val completed: Boolean = false, // 是否已完成
    val metadata: Map<String, Any> = emptyMap() // 扩展数据
) {

    /**
     * 检查挑战是否已过期（基于日期）
     */
    fun isExpired(): Boolean {
        return try {
            val challengeDate = LocalDate.parse(date)
            val today = LocalDate.now()
            challengeDate.isBefore(today)
        } catch (e: Exception) {
            true // 解析失败视为过期
        }
    }

    /**
     * 计算进度百分比
     */
    fun calculateProgressPercentage(): Float {
        return if (target > 0) {
            (progress / target).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    /**
     * 检查是否可以完成
     */
    fun canComplete(): Boolean {
        return !completed && !isExpired() && progress >= target
    }
}