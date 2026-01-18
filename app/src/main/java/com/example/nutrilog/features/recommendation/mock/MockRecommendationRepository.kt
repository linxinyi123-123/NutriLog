package com.example.nutrilog.features.recommendation.mock

import com.example.nutrilog.features.recommendation.challenge.DailyChallenge
import com.example.nutrilog.features.recommendation.interfaces.RecommendationRepository
import com.example.nutrilog.features.recommendation.model.Recommendation
import com.example.nutrilog.features.recommendation.model.gamification.Achievement
import java.time.LocalDate

class MockRecommendationRepository : RecommendationRepository {
    private val processedRecommendations = mutableSetOf<Long>()
    private val recommendations = mutableMapOf<Long, Recommendation>()
    private val challenges = mutableMapOf<Long, DailyChallenge>()
    private val achievements = mutableMapOf<Long, Achievement>()

    init {
        // 初始化时加载Mock数据
        loadMockData()
    }

    private fun loadMockData() {
        // 加载推荐数据
        EnhancedMockData.generateDiverseRecommendations(1L).forEach {
            recommendations[it.id] = it
        }

        // 加载成就数据
        EnhancedMockData.generateAllAchievements().forEach {
            achievements[it.id] = it
        }
    }

    override suspend fun getProcessedRecommendations(userId: Long): List<Long> {
        return processedRecommendations.toList()
    }

    override suspend fun saveRecommendation(recommendation: Recommendation) {
        recommendations[recommendation.id] = recommendation
    }

    override suspend fun markAsRead(recommendationId: Long) {
        processedRecommendations.add(recommendationId)
        // 更新推荐状态
        recommendations[recommendationId]?.let { rec ->
            val updated = rec.copy(/* 可以添加已读标记 */)
            recommendations[recommendationId] = updated
        }
    }

    override suspend fun markAsApplied(recommendationId: Long) {
        processedRecommendations.add(recommendationId)
        // 更新推荐状态
        recommendations[recommendationId]?.let { rec ->
            val updated = rec.copy(/* 可以添加已应用标记 */)
            recommendations[recommendationId] = updated
        }
    }

    override suspend fun getTodaysCompletedChallenges(userId: Long, today: LocalDate): List<DailyChallenge> {
        return challenges.values.filter { it.completed }.take(2)
    }

    override suspend fun getUserAchievements(userId: Long): List<Achievement> {
        return achievements.values.toList()
    }

    override suspend fun updateChallengeProgress(challengeId: Long, progress: Float) {
        challenges[challengeId]?.let { challenge ->
            val updated = challenge.copy(progress = progress)
            challenges[challengeId] = updated
        }
    }

    override suspend fun markChallengeCompleted(challengeId: Long) {
        challenges[challengeId]?.let { challenge ->
            val updated = challenge.copy(completed = true)
            challenges[challengeId] = updated
        }
    }

    override suspend fun getRecommendation(recommendationId: Long): Recommendation? {
        return recommendations[recommendationId]
    }

    // 新增方法：获取所有推荐（用于新界面）
    suspend fun getAllRecommendations(userId: Long): List<Recommendation> {
        return recommendations.values.toList()
    }
}