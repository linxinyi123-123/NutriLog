// app/src/main/java/com/nutrilog/features/recommendation/challenge/WeeklyChallenge.kt
package com.example.nutrilog.features.recommendation.challenge

/**
 * 每周挑战数据模型
 */
data class WeeklyChallenge(
    val id: Long,
    val userId: Long,
    val weekStartDate: String,     // 周开始日期，格式：2024-01-15
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
     * 获取周数（相对开始日期的周数）
     */
    fun getWeekNumber(): Int {
        // 简化实现：从metadata中获取或基于日期计算
        return metadata["weekNumber"] as? Int ?: 1
    }

    /**
     * 检查挑战是否已过期（基于日期）
     */
    fun isExpired(): Boolean {
        val weekStart = java.time.LocalDate.parse(weekStartDate)
        val weekEnd = weekStart.plusDays(6)
        val today = java.time.LocalDate.now()

        return today.isAfter(weekEnd)
    }
}