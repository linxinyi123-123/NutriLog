package com.example.nutrilog.features.recommendation.factory

import com.example.nutrilog.features.recommendation.algorithm.*
import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.model.*
import com.example.nutrilog.features.recommendation.model.improvement.ImprovementPlan
import com.example.nutrilog.features.recommendation.repository.ImprovementPlanRepository
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull

/**
 * 推荐工厂，负责生成和管理所有推荐
 * D7更新：集成改善计划
 */
class RecommendationFactory(
    private val gapRecommender: NutritionalGapRecommender = NutritionalGapRecommender(),
    private val goalRecommender: GoalBasedRecommender = GoalBasedRecommender(),
    private val contextRecommender: ContextAwareRecommender = ContextAwareRecommender(),
    private val planGenerator: ImprovementPlanGenerator = ImprovementPlanGenerator(),
    private val planRepository: ImprovementPlanRepository? = null
) {

    /**
     * 生成所有类型的推荐（异步）
     */
    suspend fun generateAllRecommendations(
        context: RecommendationContext
    ): List<Recommendation> {
        return withContext(Dispatchers.Default) {
            val allRecommendations = mutableListOf<Recommendation>()

            // 1. 基于营养缺口的推荐
            val gapRecommendations = gapRecommender.generateRecommendations(
                context.nutritionalGaps ?: emptyList(),
                context
            )
            allRecommendations.addAll(gapRecommendations)

            // 2. 基于健康目标的推荐
            val goalRecommendations = goalRecommender.generateGoalRecommendations(
                context.healthGoals ?: emptyList(),
                context
            )
            allRecommendations.addAll(goalRecommendations)

            // 3. 基于场景的推荐
            val contextRecommendations = contextRecommender.generateContextRecommendations(context)
            allRecommendations.addAll(contextRecommendations)

            // 4. 改善计划相关推荐
            val planRecommendations = generatePlanRecommendations(context)
            allRecommendations.addAll(planRecommendations)

            // 去重、排序、限制数量
            processRecommendations(allRecommendations)
        }
    }

    /**
     * 生成改善计划相关推荐
     */
    private suspend fun generatePlanRecommendations(
        context: RecommendationContext
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 检查是否有活跃的健康目标但没有改善计划
        val activeGoals = context.healthGoals?.filter { it.status == GoalStatus.ACTIVE } ?: emptyList()

        // 获取当前用户的所有活跃计划
        val activePlans = planRepository?.getActivePlans(context.userId)?.firstOrNull() ?: emptyList()

        activeGoals.forEach { goal ->
            // 检查是否已有该目标的改善计划
            val hasActivePlan = activePlans.any { plan ->
                plan.healthGoalId == goal.id
            }

            if (!hasActivePlan) {  // 这里应该不再报错了
                // 推荐创建改善计划
                recommendations.add(
                    Recommendation(
                        id = System.currentTimeMillis(),
                        type = RecommendationType.MEAL_PLAN,
                        title = "为${goal.type.name}目标创建计划",
                        description = "检测到您有活跃的${goal.type.name}目标，但没有对应的改善计划。创建一个4周改善计划来系统性地达成目标吧！",
                        priority = Priority.MEDIUM,
                        confidence = 0.8f,
                        reason = "基于活跃的健康目标",
                        actions = listOf(
                            Action.ShowFoodDetails(-300), // 特殊ID表示创建计划
                            Action.DismissRecommendation("稍后提醒")
                        ),
                        metadata = mapOf(
                            "goalId" to goal.id,
                            "goalType" to goal.type.name,
                            "action" to "create_improvement_plan"
                        )
                    )
                )
            }
        }

        // 检查现有计划的进度
        activePlans.forEach { plan ->
            if (plan.progress < 0.3f && plan.getDaysPassed() > 7) {
                // 进度缓慢的计划
                recommendations.add(
                    Recommendation(
                        id = System.currentTimeMillis(),
                        type = RecommendationType.HABIT_IMPROVEMENT,
                        title = "计划进度提醒",
                        description = "您的'${plan.title}'计划进度较慢。建议检查每日任务完成情况，或调整计划难度。",
                        priority = Priority.LOW,
                        confidence = 0.6f,
                        reason = "基于改善计划进度",
                        actions = listOf(
                            Action.DismissRecommendation("知道了")
                        ),
                        metadata = mapOf(
                            "planId" to plan.id,
                            "progress" to plan.progress
                        )
                    )
                )
            }

            if (plan.progress > 0.8f && plan.getDaysRemaining() < 7) {
                // 即将完成的计划
                recommendations.add(
                    Recommendation(
                        id = System.currentTimeMillis(),
                        type = RecommendationType.HABIT_IMPROVEMENT,
                        title = "计划即将完成",
                        description = "恭喜！您的'${plan.title}'计划即将完成。继续坚持最后几天！",
                        priority = Priority.LOW,
                        confidence = 0.9f,
                        reason = "基于改善计划进度",
                        actions = listOf(
                            Action.DismissRecommendation("好的")
                        ),
                        metadata = mapOf(
                            "planId" to plan.id,
                            "progress" to plan.progress,
                            "daysRemaining" to plan.getDaysRemaining()
                        )
                    )
                )
            }
        }

        return recommendations
    }

    /**
     * 创建改善计划
     */
    suspend fun createImprovementPlan(
        goalId: Long,
        context: RecommendationContext
    ): ImprovementPlan? {
        val goal = context.healthGoals?.find { it.id == goalId } ?: return null

        // 生成计划
        val plan = planGenerator.generatePlanForGoal(goal, context)

        // 保存计划
        planRepository?.savePlan(plan)

        return plan
    }

    /**
     * 获取用户的改善计划
     */
    suspend fun getUserImprovementPlans(userId: Long): List<ImprovementPlan> {
        return planRepository?.getUserPlans(userId)?.firstOrNull() ?: emptyList()
    }

    /**
     * 处理推荐列表：去重、排序、限制
     */
    private fun processRecommendations(
        recommendations: List<Recommendation>
    ): List<Recommendation> {
        if (recommendations.isEmpty()) {
            return emptyList()
        }

        // 1. 去重
        val deduplicated = deduplicateRecommendations(recommendations)

        // 2. 排序（优先级高 -> 低，置信度高 -> 低）
        val sorted = deduplicated.sortedWith(
            compareByDescending<Recommendation> { it.priority }
                .thenByDescending { it.confidence }
        )

        // 3. 限制数量（最多10条）
        return sorted.take(10)
    }

    /**
     * 推荐去重逻辑
     */
    private fun deduplicateRecommendations(
        recommendations: List<Recommendation>
    ): List<Recommendation> {
        val seen = mutableSetOf<String>()
        return recommendations.filter { recommendation ->
            // 使用类型、标题和主要元数据作为去重键
            val key = buildString {
                append(recommendation.type)
                append(":")
                append(recommendation.title.hashCode())

                // 添加关键元数据
                when (recommendation.type) {
                    RecommendationType.NUTRITION_GAP -> {
                        val nutrient = recommendation.metadata["nutrient"] as? String
                        nutrient?.let { append(":$it") }
                    }
                    RecommendationType.MEAL_PLAN -> {
                        val goalType = recommendation.metadata["goalType"] as? String
                        goalType?.let { append(":$it") }
                    }
                    else -> {}
                }
            }

            if (seen.contains(key)) {
                false
            } else {
                seen.add(key)
                true
            }
        }
    }

    /**
     * 获取特定类型的推荐
     */
    suspend fun getRecommendationsByType(
        context: RecommendationContext,
        type: RecommendationType
    ): List<Recommendation> {
        val allRecommendations = generateAllRecommendations(context)
        return allRecommendations.filter { it.type == type }
    }

    /**
     * 获取基于场景的推荐
     */
    suspend fun getContextBasedRecommendations(
        context: RecommendationContext
    ): List<Recommendation> {
        return contextRecommender.generateContextRecommendations(context)
    }

    /**
     * 获取高优先级推荐（用于通知）
     */
    suspend fun getHighPriorityRecommendations(
        context: RecommendationContext,
        limit: Int = 3
    ): List<Recommendation> {
        val allRecommendations = generateAllRecommendations(context)
        return allRecommendations
            .filter { it.priority == Priority.HIGH }
            .take(limit)
    }

    /**
     * 获取今日推荐（综合排序前N条）
     */
    suspend fun getTodayRecommendations(
        context: RecommendationContext,
        limit: Int = 5
    ): List<Recommendation> {
        val allRecommendations = generateAllRecommendations(context)
        return allRecommendations.take(limit)
    }

    /**
     * 检查是否有新的高优先级推荐
     */
    suspend fun hasNewHighPriorityRecommendations(
        context: RecommendationContext,
        seenRecommendationIds: Set<Long>
    ): Boolean {
        val highPriorityRecs = getHighPriorityRecommendations(context)
        return highPriorityRecs.any { it.id !in seenRecommendationIds }
    }

    /**
     * 获取当前用餐场景描述
     */
    fun getCurrentScenarioDescription(context: RecommendationContext): String {
        return contextRecommender.getCurrentScenario(context)
    }
}