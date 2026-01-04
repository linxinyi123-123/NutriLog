package com.example.nutrilog.features.recommendation.model.improvement

import com.example.nutrilog.features.recommendation.model.GoalType
import com.example.nutrilog.features.recommendation.model.HealthGoal
import java.time.LocalDate

/**
 * 改善计划
 */
data class ImprovementPlan(
    val id: String,                     // 计划ID
    val userId: Long,                   // 用户ID
    val title: String,                  // 计划标题
    val goalType: GoalType,             // 目标类型
    val healthGoalId: Long? = null,     // 关联的健康目标ID
    val duration: Int,                  // 总时长（天）
    val startDate: LocalDate,           // 开始日期
    val endDate: LocalDate,             // 结束日期
    val currentWeek: Int = 1,           // 当前周数
    val totalWeeks: Int,                // 总周数
    val weeklyPlans: List<WeeklyPlan>,  // 周计划列表
    val dailyTemplates: List<DailyTask> = emptyList(), // 每日任务模板
    val status: PlanStatus = PlanStatus.ACTIVE, // 计划状态
    val progress: Float = 0f,           // 总进度（0-1）
    val completedWeeks: Set<Int> = emptySet(), // 已完成的周数
    val milestones: List<Milestone> = emptyList(), // 里程碑
    val createdAt: LocalDate = LocalDate.now(), // 创建时间
    val updatedAt: LocalDate = LocalDate.now(), // 更新时间
    val notes: String? = null           // 备注
) {
    /**
     * 获取已过去的天数
     */
    fun getDaysPassed(): Int {
        val today = LocalDate.now()
        return if (today.isBefore(startDate)) {
            0
        } else {
            startDate.until(today).days.coerceAtMost(duration)
        }
    }

    /**
     * 获取剩余天数
     */
    fun getDaysRemaining(): Int {
        return duration - getDaysPassed()
    }

    /**
     * 检查是否已过期
     */
    fun isExpired(): Boolean {
        return LocalDate.now().isAfter(endDate)
    }

    /**
     * 检查本周是否已完成
     */
    fun isCurrentWeekCompleted(completedTasks: List<String>): Boolean {
        val currentWeeklyPlan = weeklyPlans.getOrNull(currentWeek - 1) ?: return false

        // 计算本周进度
        val totalRequiredTasks = currentWeeklyPlan.dailyTasks.count { it.isRequired }
        if (totalRequiredTasks == 0) return false

        val completedRequiredTasks = currentWeeklyPlan.dailyTasks
            .filter { it.isRequired }
            .count { task -> task.id in completedTasks }

        val progress = completedRequiredTasks.toFloat() / totalRequiredTasks
        return progress >= 0.7f // 70%任务完成视为本周完成
    }

    /**
     * 获取当前周的开始日期
     */
    fun getCurrentWeekStartDate(): LocalDate {
        return startDate.plusDays((currentWeek - 1) * 7L)
    }

    /**
     * 获取当前周的结束日期
     */
    fun getCurrentWeekEndDate(): LocalDate {
        return startDate.plusDays((currentWeek * 7L) - 1).coerceAtMost(endDate)
    }
}

/**
 * 里程碑
 */
data class Milestone(
    val id: String,             // 里程碑ID
    val title: String,          // 里程碑标题
    val description: String,    // 描述
    val weekNumber: Int,        // 对应的周数
    val rewardPoints: Int = 0,  // 奖励积分
    val achieved: Boolean = false, // 是否达成
    val achievedAt: LocalDate? = null // 达成时间
)

/**
 * 改善计划统计
 */
data class PlanStatistics(
    val totalPlans: Int = 0,                // 总计划数
    val completedPlans: Int = 0,            // 已完成计划数
    val activePlans: Int = 0,               // 进行中计划数
    val averageCompletionRate: Float = 0f,  // 平均完成率
    val totalPointsEarned: Int = 0,         // 获得总积分
    val favoriteGoalType: GoalType? = null, // 最常选择的目标类型
    val longestStreak: Int = 0              // 最长连续完成天数
)