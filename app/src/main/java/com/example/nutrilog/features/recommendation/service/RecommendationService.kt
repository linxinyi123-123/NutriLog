
// app/src/main/java/com/nutrilog/features/recommendation/service/RecommendationService.kt
package com.example.nutrilog.features.recommendation.service

import com.example.nutrilog.features.recommendation.model.Recommendation
import com.example.nutrilog.features.recommendation.model.GoalType
import com.example.nutrilog.features.recommendation.model.improvement.ImprovementPlan
import com.example.nutrilog.features.recommendation.model.gamification.Achievement
import com.example.nutrilog.features.recommendation.challenge.DailyChallenge
import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import kotlinx.coroutines.flow.Flow

/**
 * 推荐系统服务接口
 * 负责生成和管理用户的个性化饮食推荐
 */
interface RecommendationService {

    /**
     * 获取用户的每日推荐
     * @param userId 用户ID
     * @return 推荐列表
     */
    suspend fun getDailyRecommendations(userId: Long): List<Recommendation>

    /**
     * 根据上下文获取推荐
     * @param userId 用户ID
     * @param context 推荐上下文
     * @return 推荐列表
     */
    suspend fun getContextRecommendations(
        userId: Long,
        context: RecommendationContext
    ): List<Recommendation>

    /**
     * 获取指定目标的改善计划
     * @param userId 用户ID
     * @param goalType 目标类型
     * @return 改善计划，如果不存在则返回null
     */
    suspend fun getImprovementPlan(
        userId: Long,
        goalType: GoalType
    ): ImprovementPlan?

    /**
     * 获取用户的每日挑战
     * @param userId 用户ID
     * @return 挑战列表
     */
    suspend fun getDailyChallenges(userId: Long): List<DailyChallenge>

    /**
     * 获取用户已解锁和可解锁的成就
     * @param userId 用户ID
     * @return 成就列表
     */
    suspend fun getUserAchievements(userId: Long): List<Achievement>

    /**
     * 标记推荐为已读
     * @param recommendationId 推荐ID
     */
    suspend fun markRecommendationRead(recommendationId: Long)

    /**
     * 标记推荐为已应用
     * @param recommendationId 推荐ID
     */
    suspend fun markRecommendationApplied(recommendationId: Long)

    /**
     * 更新挑战进度
     * @param challengeId 挑战ID
     * @param progress 进度值 (0-1)
     */
    suspend fun updateChallengeProgress(challengeId: Long, progress: Float)

    /**
     * 跟踪改善计划进度
     * @param planId 计划ID
     * @param userId 用户ID
     */
    suspend fun trackPlanProgress(planId: String, userId: Long)

    /**
     * 标记计划任务为完成
     * @param planId 计划ID
     * @param userId 用户ID
     * @param taskId 任务ID
     */
    suspend fun markPlanTaskComplete(planId: String, userId: Long, taskId: String)

    /**
     * 获取用户当前活跃的改善计划
     * @param userId 用户ID
     * @return 改善计划列表
     */
    suspend fun getActivePlans(userId: Long): Flow<List<ImprovementPlan>>

    /**
     * 获取计划详情
     * @param planId 计划ID
     * @param userId 用户ID
     * @return 改善计划，如果不存在则返回null
     */
    suspend fun getPlanDetails(planId: String, userId: Long): ImprovementPlan?

    /**
     * 创建新的改善计划
     * @param userId 用户ID
     * @param goalType 目标类型
     * @return 新创建的改善计划，如果无法创建则返回null
     */
    suspend fun createImprovementPlan(userId: Long, goalType: GoalType): ImprovementPlan?
}
