package com.example.nutrilog.features.recommendation.algorithm

import com.example.nutrilog.features.recommendation.model.improvement.*
import com.example.nutrilog.features.recommendation.repository.DailyProgress
import com.example.nutrilog.features.recommendation.repository.ImprovementPlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * 计划跟踪器
 */
class PlanTracker(
    private val planRepository: ImprovementPlanRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    /**
     * 跟踪计划进度
     */
    fun trackPlanProgress(planId: String, userId: Long) {
        scope.launch {
            val plan = planRepository.getPlan(userId, planId) ?: return@launch

            // 检查计划是否已过期
            if (plan.isExpired() && plan.status == PlanStatus.ACTIVE) {
                planRepository.updatePlanStatus(plan.id, PlanStatus.FAILED.name)
                return@launch
            }

            // 检查是否所有周都已完成
            if (plan.completedWeeks.size == plan.totalWeeks && plan.status == PlanStatus.ACTIVE) {
                planRepository.updatePlanStatus(plan.id, PlanStatus.COMPLETED.name)
                return@launch
            }

            // 获取今天的进度
            val today = LocalDate.now()
            val todayProgress = planRepository.getDailyProgress(userId, planId, today)

            // 分析计划完成情况
            val progress = analyzePlanProgress(plan, todayProgress)

            // 更新计划进度
            planRepository.updatePlanProgress(planId, progress)

            // 检查是否完成本周目标
            checkWeeklyCompletion(plan, userId)
        }
    }

    /**
     * 标记任务完成
     */
    fun markTaskComplete(
        planId: String,
        userId: Long,
        taskId: String,
        date: LocalDate = LocalDate.now()
    ) {
        scope.launch {
            // 获取当天的进度
            val dailyProgress = planRepository.getDailyProgress(userId, planId, date)
            val completedTasks = dailyProgress?.completedTasks?.toMutableList() ?: mutableListOf()

            // 添加任务
            if (!completedTasks.contains(taskId)) {
                completedTasks.add(taskId)
            }

            // 保存进度
            planRepository.saveDailyProgress(
                userId = userId,
                planId = planId,
                date = date,
                completedTasks = completedTasks
            )

            // 更新计划进度
            trackPlanProgress(planId, userId)
        }
    }

    /**
     * 分析计划进度
     */
    private fun analyzePlanProgress(
        plan: ImprovementPlan,
        todayProgress: DailyProgress?
    ): Float {
        val daysPassed = plan.getDaysPassed()
        if (daysPassed <= 0) return 0f

        // 获取最近7天的进度
        val recentProgress = mutableListOf<DailyProgress>()
        for (i in 0 until daysPassed.coerceAtMost(7)) {
            val date = LocalDate.now().minusDays(i.toLong())
            // 这里应该从数据库获取，简化处理
            if (i == 0 && todayProgress != null) {
                recentProgress.add(todayProgress)
            }
        }

        if (recentProgress.isEmpty()) return plan.progress

        // 计算平均完成率
        val averageProgress = recentProgress
            .map { it.completionRate }
            .average()
            .toFloat()

        // 结合之前的进度（加权平均）
        return (plan.progress * 0.3f + averageProgress * 0.7f).coerceIn(0f, 1f)
    }

    /**
     * 检查每周完成情况
     */
    private suspend fun checkWeeklyCompletion(plan: ImprovementPlan, userId: Long) {
        if (plan.currentWeek > plan.totalWeeks) return

        // 获取本周的每日进度
        val weekStart = calculateWeekStartDate(plan.startDate, plan.currentWeek)
        val weekEnd = calculateWeekEndDate(plan.startDate, plan.currentWeek)

        val completedTasksThisWeek = mutableListOf<String>()

        // 获取本周每天的进度
        var date = weekStart
        while (!date.isAfter(weekEnd)) {
            val progress = planRepository.getDailyProgress(userId, plan.id, date)
            progress?.completedTasks?.let { completedTasksThisWeek.addAll(it) }
            date = date.plusDays(1)
        }

        // 检查本周是否完成
        val currentWeeklyPlan = plan.weeklyPlans.getOrNull(plan.currentWeek - 1)
        if (currentWeeklyPlan != null) {
            val isWeekCompleted = plan.isCurrentWeekCompleted(completedTasksThisWeek)

            if (isWeekCompleted && plan.currentWeek !in plan.completedWeeks) {
                // 标记本周完成
                planRepository.markWeekCompleted(
                    plan.id,
                    plan.currentWeek,
                    plan.completedWeeks
                )

                // 检查是否所有周都完成了
                val updatedCompletedWeeks = plan.completedWeeks + plan.currentWeek
                if (updatedCompletedWeeks.size == plan.totalWeeks) {
                    // 所有周都完成，更新计划状态
                    planRepository.updatePlanStatus(plan.id, PlanStatus.COMPLETED.name)
                }

                // 发送祝贺通知
                sendCongratulationNotification(userId, plan.currentWeek)

                // 检查里程碑
                checkMilestones(plan, plan.currentWeek, userId)
            }
        }
    }

    /**
     * 检查里程碑
     */
    private fun checkMilestones(plan: ImprovementPlan, weekNumber: Int, userId: Long) {
        plan.milestones.forEach { milestone ->
            if (milestone.weekNumber == weekNumber && !milestone.achieved) {
                // 达成里程碑
                // 这里应该更新里程碑状态
                sendMilestoneNotification(userId, milestone)
            }
        }
    }

    /**
     * 生成进度报告
     */
    private fun generateProgressReport(plan: ImprovementPlan, progress: Float) {
        val report = ProgressReport(
            planId = plan.id,
            date = LocalDate.now(),
            overallProgress = progress,
            currentWeek = plan.currentWeek,
            daysPassed = plan.getDaysPassed(),
            daysRemaining = plan.getDaysRemaining(),
            weeklyProgress = calculateWeeklyProgress(plan),
            suggestions = generateSuggestions(plan, progress)
        )

        // 保存或发送报告
        saveProgressReport(report)
    }

    /**
     * 计算周进度
     */
    private fun calculateWeeklyProgress(plan: ImprovementPlan): Map<Int, Float> {
        val progressMap = mutableMapOf<Int, Float>()

        // 简化实现
        plan.completedWeeks.forEach { week ->
            progressMap[week] = 1.0f
        }

        if (plan.currentWeek <= plan.totalWeeks) {
            progressMap[plan.currentWeek] = plan.progress
        }

        return progressMap
    }

    /**
     * 生成改进建议
     */
    private fun generateSuggestions(plan: ImprovementPlan, progress: Float): List<String> {
        val suggestions = mutableListOf<String>()

        when {
            progress < 0.3f -> {
                suggestions.add("进度较慢，建议从简单的任务开始建立信心")
                suggestions.add("尝试每天完成一个必做任务")
            }
            progress < 0.7f -> {
                suggestions.add("进度良好，继续保持")
                suggestions.add("可以尝试挑战一些可选任务")
            }
            else -> {
                suggestions.add("进度优秀！继续保持这样的势头")
                suggestions.add("考虑增加一些新的挑战")
            }
        }

        return suggestions
    }

    /**
     * 计算周开始日期
     */
    private fun calculateWeekStartDate(startDate: LocalDate, weekNumber: Int): LocalDate {
        return startDate.plusDays((weekNumber - 1) * 7L)
    }

    /**
     * 计算周结束日期
     */
    private fun calculateWeekEndDate(startDate: LocalDate, weekNumber: Int): LocalDate {
        return startDate.plusDays((weekNumber * 7L) - 1)
    }

    /**
     * 发送祝贺通知
     */
    private fun sendCongratulationNotification(userId: Long, weekNumber: Int) {
        // 实现通知逻辑
        println("祝贺用户 ${userId}完成第 ${weekNumber}周计划！")
    }

    /**
     * 发送里程碑通知
     */
    private fun sendMilestoneNotification(userId: Long, milestone: Milestone) {
        // 实现通知逻辑
        println("用户${userId}达成里程碑：${milestone.title}")
    }

    /**
     * 保存进度报告
     */
    private fun saveProgressReport(report: ProgressReport) {
        // 实现保存逻辑
        println("保存进度报告：${report.planId} - 进度${report.overallProgress}")
    }
}

/**
 * 进度报告
 */
data class ProgressReport(
    val planId: String,
    val date: LocalDate,
    val overallProgress: Float,
    val currentWeek: Int,
    val daysPassed: Int,
    val daysRemaining: Int,
    val weeklyProgress: Map<Int, Float>,
    val suggestions: List<String>
)