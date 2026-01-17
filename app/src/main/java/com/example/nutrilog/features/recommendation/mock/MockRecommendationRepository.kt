package com.example.nutrilog.features.recommendation.mock

import com.example.nutrilog.features.recommendation.challenge.DailyChallenge
import com.example.nutrilog.features.recommendation.interfaces.RecommendationRepository
import com.example.nutrilog.features.recommendation.model.Recommendation
import com.example.nutrilog.features.recommendation.model.gamification.Achievement
import java.time.LocalDate

// 快速创建缺失的基础类
// features/recommendation/mock/MockRecommendationRepository.kt
class MockRecommendationRepository : RecommendationRepository {
    private val processedRecommendations = mutableSetOf<Long>()
    private val recommendations = mutableMapOf<Long, Recommendation>()
    private val challenges = mutableMapOf<Long, DailyChallenge>()
    private val achievements = mutableMapOf<Long, Achievement>()

    override suspend fun getProcessedRecommendations(userId: Long): List<Long> {
        return processedRecommendations.toList()
    }

    override suspend fun saveRecommendation(recommendation: Recommendation) {
        recommendations[recommendation.id] = recommendation
    }

    override suspend fun markAsRead(recommendationId: Long) {
        processedRecommendations.add(recommendationId)
    }

    override suspend fun markAsApplied(recommendationId: Long) {
        processedRecommendations.add(recommendationId)
    }

    override suspend fun getTodaysCompletedChallenges(userId: Long, today: LocalDate): List<DailyChallenge> {
        return challenges.values.filter { it.completed }.take(2)
    }

    override suspend fun getUserAchievements(userId: Long): List<Achievement> {
        return achievements.values.toList()
    }

    override suspend fun updateChallengeProgress(challengeId: Long, progress: Float) {
        challenges[challengeId]?.progress = progress
    }

    override suspend fun markChallengeCompleted(challengeId: Long) {
        challenges[challengeId]?.completed = true
    }

    override suspend fun getRecommendation(recommendationId: Long): Recommendation? {
        return recommendations[recommendationId]
    }
}
