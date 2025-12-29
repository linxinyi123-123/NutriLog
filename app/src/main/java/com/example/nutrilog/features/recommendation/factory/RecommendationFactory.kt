package com.example.nutrilog.features.recommendation.factory

import com.example.nutrilog.features.recommendation.algorithm.*
import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.interfaces.NutritionalGap
import com.example.nutrilog.features.recommendation.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/**
 * 推荐工厂，负责生成和管理所有推荐
 */
class RecommendationFactory(
    private val gapRecommender: NutritionalGapRecommender = NutritionalGapRecommender(),
    private val goalRecommender: GoalBasedRecommender = GoalBasedRecommender()
    // 注意：ContextAwareRecommender将在D6实现
) {

    private val ioScope = CoroutineScope(Dispatchers.IO)

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
                context.nutritionalGaps,
                context
            )
            allRecommendations.addAll(gapRecommendations)

            // 2. 基于健康目标的推荐
            val goalRecommendations = goalRecommender.generateGoalRecommendations(
                context.healthGoals,
                context
            )
            allRecommendations.addAll(goalRecommendations)

            // 3. 基于场景的推荐（将在D6实现）
            // val contextRecommendations = contextRecommender.generateContextRecommendations(context)
            // allRecommendations.addAll(contextRecommendations)

            // 去重、排序、限制数量
            processRecommendations(allRecommendations)
        }
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
}