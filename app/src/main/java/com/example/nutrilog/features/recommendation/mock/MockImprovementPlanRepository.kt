// features/recommendation/repository/MockImprovementPlanRepository.kt
package com.example.nutrilog.features.recommendation.repository

import com.example.nutrilog.features.recommendation.model.improvement.*
import com.example.nutrilog.features.recommendation.model.GoalType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import com.google.gson.Gson

/**
 * Mock改善计划仓库实现
 */
class MockImprovementPlanRepository : ImprovementPlanRepository {

    private val plans = mutableMapOf<String, ImprovementPlan>()
    private val dailyProgressMap = mutableMapOf<String, DailyProgress>()
    private val activePlansFlow = MutableStateFlow<List<ImprovementPlan>>(emptyList())

    override suspend fun savePlan(plan: ImprovementPlan) {
        plans[plan.id] = plan
        updateActivePlansFlow()
    }

    override fun getUserPlans(userId: Long): Flow<List<ImprovementPlan>> {
        return activePlansFlow.map { plans ->
            plans.filter { it.userId == userId }
        }
    }

    override fun getActivePlans(userId: Long): Flow<List<ImprovementPlan>> {
        val userActivePlans = plans.values.filter {
            it.userId == userId && it.status == PlanStatus.ACTIVE
        }
        activePlansFlow.value = userActivePlans
        return activePlansFlow
    }

    override suspend fun getPlan(userId: Long, planId: String): ImprovementPlan? {
        val plan = plans[planId]
        // 检查计划是否属于该用户
        return if (plan?.userId == userId) plan else null
    }

    override suspend fun updatePlanProgress(planId: String, progress: Float) {
        plans[planId]?.let { plan ->
            val updatedPlan = plan.copy(progress = progress)
            plans[planId] = updatedPlan
            updateActivePlansFlow()
        }
    }

    override suspend fun markWeekCompleted(
        planId: String,
        weekNumber: Int,
        completedWeeks: Set<Int>
    ) {
        plans[planId]?.let { plan ->
            val updatedCompletedWeeks = plan.completedWeeks + weekNumber
            val updatedPlan = plan.copy(
                completedWeeks = updatedCompletedWeeks,
                currentWeek = if (plan.currentWeek < plan.totalWeeks) plan.currentWeek + 1 else plan.currentWeek
            )
            plans[planId] = updatedPlan
            updateActivePlansFlow()
        }
    }

    override suspend fun deletePlan(planId: String) {
        plans.remove(planId)
        // 删除相关进度数据
        dailyProgressMap.keys.filter { it.contains(planId) }.forEach { dailyProgressMap.remove(it) }
        updateActivePlansFlow()
    }

    override suspend fun saveDailyProgress(
        userId: Long,
        planId: String,
        date: LocalDate,
        completedTasks: List<String>,
        nutritionData: Map<String, Double>,
        notes: String?
    ) {
        val progressId = "${userId}_${planId}_${date}"
        val progress = DailyProgress(
            id = progressId,
            userId = userId,
            planId = planId,
            date = date,
            completedTasks = completedTasks,
            nutritionData = nutritionData,
            notes = notes,
            createdAt = System.currentTimeMillis()
        )
        dailyProgressMap[progressId] = progress

        // 更新计划进度
        updatePlanOverallProgress(planId, date)
    }

    override suspend fun getDailyProgress(
        userId: Long,
        planId: String,
        date: LocalDate
    ): DailyProgress? {
        val progressId = "${userId}_${planId}_${date}"
        return dailyProgressMap[progressId]
    }

    override fun getPlanProgressHistory(userId: Long, planId: String): Flow<List<DailyProgress>> {
        val progressList = dailyProgressMap.values.filter {
            it.userId == userId && it.planId == planId
        }.sortedByDescending { it.date }
        return MutableStateFlow(progressList)
    }

    override suspend fun getUserPlanStatistics(userId: Long): PlanStatistics {
        val userPlans = plans.values.filter { it.userId == userId }
        val totalPlans = userPlans.size
        val completedPlans = userPlans.count { it.status == PlanStatus.COMPLETED }
        val activePlans = userPlans.count { it.status == PlanStatus.ACTIVE }

        val averageCompletionRate = if (totalPlans > 0) {
            userPlans.map { it.progress }.average().toFloat()
        } else 0f

        val goalTypeCounts = userPlans.groupingBy { it.goalType }.eachCount()
        val favoriteGoalType = goalTypeCounts.maxByOrNull { it.value }?.key

        val longestStreak = calculateLongestStreak(userPlans)

        return PlanStatistics(
            totalPlans = totalPlans,
            completedPlans = completedPlans,
            activePlans = activePlans,
            averageCompletionRate = averageCompletionRate,
            favoriteGoalType = favoriteGoalType,
            longestStreak = longestStreak
        )
    }

    override suspend fun updatePlanStatus(planId: String, status: String) {
        plans[planId]?.let { plan ->
            val planStatus = try {
                PlanStatus.valueOf(status)
            } catch (e: IllegalArgumentException) {
                PlanStatus.ACTIVE
            }
            val updatedPlan = plan.copy(status = planStatus)
            plans[planId] = updatedPlan
            updateActivePlansFlow()
        }
    }

    private suspend fun updatePlanOverallProgress(planId: String, currentDate: LocalDate) {
        val plan = plans[planId] ?: return
        val daysPassed = plan.getDaysPassed()
        if (daysPassed <= 0) return

        // 获取最近N天的进度
        val recentProgress = mutableListOf<DailyProgress>()
        for (i in 0 until daysPassed.coerceAtMost(7)) {
            val date = currentDate.minusDays(i.toLong())
            val progressId = "${plan.userId}_${planId}_${date}"
            dailyProgressMap[progressId]?.let { recentProgress.add(it) }
        }

        if (recentProgress.isEmpty()) return

        // 计算平均完成率
        val averageProgress = recentProgress
            .map { it.completionRate }
            .average()
            .toFloat()

        // 更新计划进度
        plans[planId] = plan.copy(progress = averageProgress)
        updateActivePlansFlow()
    }

    private fun calculateLongestStreak(plans: List<ImprovementPlan>): Int {
        return plans.maxOfOrNull { plan ->
            // 计算每个计划的连续天数（基于进度）
            val progressDays = (plan.progress * plan.duration).toInt()
            progressDays
        } ?: 0
    }

    private fun updateActivePlansFlow() {
        val activePlans = plans.values.filter { it.status == PlanStatus.ACTIVE }
        activePlansFlow.value = activePlans
    }
}

// 简化模型
data class PlanStatistics(
    val totalPlans: Int,
    val completedPlans: Int,
    val activePlans: Int,
    val averageCompletionRate: Float,
    val favoriteGoalType: GoalType?,
    val longestStreak: Int
)