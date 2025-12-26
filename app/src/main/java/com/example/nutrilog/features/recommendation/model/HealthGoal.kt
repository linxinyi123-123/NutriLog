// app/src/main/java/com/nutrilog/features/recommendation/model/HealthGoal.kt
package com.example.nutrilog.features.recommendation.model

data class HealthGoal(
    val id: Long,
    val userId: Long,
    val type: GoalType,           // 目标类型
    val target: GoalTarget,       // 目标值
    val startDate: String,        // 开始日期
    val endDate: String,          // 结束日期
    val progress: Float,          // 进度(0-1)
    val milestones: List<Milestone>, // 里程碑
    val status: GoalStatus       // 状态
)

enum class GoalType {
    WEIGHT_LOSS,      // 减重
    WEIGHT_GAIN,      // 增重
    MUSCLE_GAIN,      // 增肌
    BODY_FAT_REDUCTION, // 减脂
    HEALTH_IMPROVEMENT,  // 健康改善
    NUTRIENT_BALANCE    // 营养均衡
}

data class GoalTarget(
    val value: Double,            // 目标数值
    val unit: String,             // 单位
    val weeklyTarget: Double? = null // 每周目标
)

data class Milestone(
    val id: Long,
    val targetValue: Double,
    val rewardPoints: Int,
    val achieved: Boolean = false,
    val achievedAt: Long? = null
)

enum class GoalStatus {
    ACTIVE, PAUSED, COMPLETED, ABANDONED, FAILED
}