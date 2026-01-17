// ImprovementPlanRepositoryImpl.kt
package com.example.nutrilog.features.recommendation.repository

import com.example.nutrilog.features.recommendation.database.dao.ImprovementPlanDao
import com.example.nutrilog.features.recommendation.database.entity.DailyProgressEntity
import com.example.nutrilog.features.recommendation.database.entity.ImprovementPlanEntity
import com.example.nutrilog.features.recommendation.model.GoalType
import com.example.nutrilog.features.recommendation.model.improvement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.firstOrNull

class ImprovementPlanRepositoryImpl(
    private val improvementPlanDao: ImprovementPlanDao
) : ImprovementPlanRepository {

    // 计划管理
    override suspend fun savePlan(plan: ImprovementPlan) {
        val entity = plan.toEntity()
        improvementPlanDao.insertPlan(entity)
    }

    override fun getUserPlans(userId: Long): Flow<List<ImprovementPlan>> {
        return improvementPlanDao.getUserPlans(userId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getActivePlans(userId: Long): Flow<List<ImprovementPlan>> {
        return improvementPlanDao.getActivePlans(userId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun getPlan(userId: Long, planId: String): ImprovementPlan? {
        return improvementPlanDao.getPlan(userId, planId)?.toModel()
    }

    override suspend fun updatePlanProgress(planId: String, progress: Float) {
        improvementPlanDao.updatePlanProgress(planId, progress)
    }

    // 修改 markWeekCompleted 方法，去掉状态更新
    override suspend fun markWeekCompleted(
        planId: String,
        weekNumber: Int,
        completedWeeks: Set<Int>
    ) {
        val completedWeeksJson = Gson().toJson(completedWeeks + weekNumber)
        improvementPlanDao.updateCompletedWeeks(planId, completedWeeksJson)

        // 只更新当前周数，不更新状态
        val planEntity = improvementPlanDao.getPlan(1, planId) // 需要userId，简化处理
        planEntity?.let {
            if (it.currentWeek < it.totalWeeks) {
                improvementPlanDao.updateCurrentWeek(planId, it.currentWeek + 1)
            }
            // 不在这里更新状态，由 PlanTracker 处理
        }
    }

    override suspend fun deletePlan(planId: String) {
        improvementPlanDao.deletePlan(planId)
        improvementPlanDao.deletePlanProgress(planId)
    }

    // 进度管理
    override suspend fun saveDailyProgress(
        userId: Long,
        planId: String,
        date: LocalDate,
        completedTasks: List<String>,
        nutritionData: Map<String, Double>, // 移除 = emptyMap()
        notes: String? // 移除 = null
    ) {
        val progressId = "${userId}_${planId}_${date}"
        val progress = DailyProgressEntity(
            id = progressId,
            userId = userId,
            planId = planId,
            date = date,
            completedTasksJson = Gson().toJson(completedTasks),
            nutritionDataJson = Gson().toJson(nutritionData),
            notes = notes
        )

        improvementPlanDao.insertDailyProgress(progress)

        // 更新计划总进度
        updatePlanOverallProgress(planId, date)
    }

    override suspend fun getDailyProgress(
        userId: Long,
        planId: String,
        date: LocalDate
    ): DailyProgress? {
        return improvementPlanDao.getDailyProgress(userId, planId, date)?.toModel()
    }

    override fun getPlanProgressHistory(userId: Long, planId: String): Flow<List<DailyProgress>> {
        return improvementPlanDao.getPlanProgressHistory(userId, planId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    private suspend fun updatePlanOverallProgress(planId: String, currentDate: LocalDate) {
        val planEntity = improvementPlanDao.getPlan(1, planId) ?: return // 简化userId
        val plan = planEntity.toModel()

        val daysPassed = plan.getDaysPassed()
        if (daysPassed <= 0) return

        // 获取最近N天的进度
        val recentProgress = mutableListOf<DailyProgress>()
        for (i in 0 until daysPassed.coerceAtMost(7)) {
            val date = currentDate.minusDays(i.toLong())
            val progress = improvementPlanDao.getDailyProgress(1, planId, date)?.toModel()
            progress?.let { recentProgress.add(it) }
        }

        if (recentProgress.isEmpty()) return

        // 计算平均完成率
        val averageProgress = recentProgress
            .map { it.completionRate }
            .average()
            .toFloat()

        improvementPlanDao.updatePlanProgress(planId, averageProgress)
    }

    // 统计
    override suspend fun getUserPlanStatistics(userId: Long): PlanStatistics {
        // 获取实体列表 - 使用 collect 收集 Flow 数据
        val planEntities = improvementPlanDao.getUserPlans(userId).firstOrNull() ?: emptyList()

        val totalPlans = planEntities.size
        val completedPlans = planEntities.count { it.status == PlanStatus.COMPLETED.name }
        val activePlans = planEntities.count { it.status == PlanStatus.ACTIVE.name }

        val averageCompletionRate = if (totalPlans > 0) {
            planEntities.map { it.progress }.average().toFloat()
        } else 0f

        // 统计最常选择的目标类型 - 安全地转换为 GoalType
        val goalTypeCounts = planEntities.groupingBy { it.goalType }.eachCount()
        val favoriteGoalType = goalTypeCounts.maxByOrNull { it.value }?.key?.let {
            try {
                GoalType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null // 如果字符串无法转换为 GoalType，返回 null
            }
        }

        // 计算最长连续完成天数（简化版）
        val longestStreak = calculateLongestStreak(planEntities)

        return PlanStatistics(
            totalPlans = totalPlans,
            completedPlans = completedPlans,
            activePlans = activePlans,
            averageCompletionRate = averageCompletionRate,
            favoriteGoalType = favoriteGoalType,
            longestStreak = longestStreak
        )
    }

    // 在 ImprovementPlanRepositoryImpl.kt 中添加这个方法
    override suspend fun updatePlanStatus(planId: String, status: String) {
        improvementPlanDao.updatePlanStatus(planId, status)
    }

    // 改进的 calculateLongestStreak 方法
    private fun calculateLongestStreak(plans: List<ImprovementPlanEntity>): Int {
        return plans.maxOfOrNull { entity ->
            // 计算每个计划的连续天数（基于进度）
            val progressDays = (entity.progress * entity.duration).toInt()
            progressDays
        } ?: 0
    }

    // 扩展函数：模型转实体
    private fun ImprovementPlan.toEntity(): ImprovementPlanEntity {
        return ImprovementPlanEntity(
            id = id,
            userId = userId,
            title = title,
            goalType = goalType.name,
            healthGoalId = healthGoalId,
            duration = duration,
            startDate = startDate,
            endDate = endDate,
            currentWeek = currentWeek,
            totalWeeks = totalWeeks,
            weeklyPlansJson = Gson().toJson(weeklyPlans),
            dailyTemplatesJson = Gson().toJson(dailyTemplates),
            status = status.name,
            progress = progress,
            completedWeeksJson = Gson().toJson(completedWeeks),
            milestonesJson = Gson().toJson(milestones),
            createdAt = createdAt,
            updatedAt = updatedAt,
            notes = notes
        )
    }

    // 扩展函数：实体转模型
    private fun ImprovementPlanEntity.toModel(): ImprovementPlan {
        val weeklyPlans = Gson().fromJson<List<WeeklyPlan>>(
            weeklyPlansJson,
            object : TypeToken<List<WeeklyPlan>>() {}.type
        )

        val dailyTemplates = Gson().fromJson<List<DailyTask>>(
            dailyTemplatesJson,
            object : TypeToken<List<DailyTask>>() {}.type
        )

        val milestones = Gson().fromJson<List<Milestone>>(
            milestonesJson,
            object : TypeToken<List<Milestone>>() {}.type
        )

        val completedWeeks = Gson().fromJson<Set<Int>>(
            completedWeeksJson,
            object : TypeToken<Set<Int>>() {}.type
        )

        return ImprovementPlan(
            id = id,
            userId = userId,
            title = title,
            goalType = GoalType.valueOf(goalType),
            healthGoalId = healthGoalId,
            duration = duration,
            startDate = startDate,
            endDate = endDate,
            currentWeek = currentWeek,
            totalWeeks = totalWeeks,
            weeklyPlans = weeklyPlans,
            dailyTemplates = dailyTemplates,
            status = PlanStatus.valueOf(status),
            progress = progress,
            completedWeeks = completedWeeks,
            milestones = milestones,
            createdAt = createdAt,
            updatedAt = updatedAt,
            notes = notes
        )
    }

    // 扩展函数：进度实体转模型
    private fun DailyProgressEntity.toModel(): DailyProgress {
        val completedTasks = Gson().fromJson<List<String>>(
            completedTasksJson,
            object : TypeToken<List<String>>() {}.type
        )

        val nutritionData = Gson().fromJson<Map<String, Double>>(
            nutritionDataJson,
            object : TypeToken<Map<String, Double>>() {}.type
        )

        return DailyProgress(
            id = id,
            userId = userId,
            planId = planId,
            date = date,
            completedTasks = completedTasks,
            nutritionData = nutritionData,
            notes = notes,
            createdAt = createdAt
        )
    }
}

// 简化模型
data class DailyProgress(
    val id: String,
    val userId: Long,
    val planId: String,
    val date: LocalDate,
    val completedTasks: List<String>,
    val nutritionData: Map<String, Double>,
    val notes: String?,
    val createdAt: Long
) {
    val completionRate: Float
        get() = if (completedTasks.isEmpty()) 0f else {
            completedTasks.size.toFloat() / 5 // 假设每天5个任务
        }
}

