// features/recommendation/interfaces/adapters.kt
package com.example.nutrilog.features.recommendation.interfaces

import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.features.recommendation.challenge.DailyChallenge
import com.example.nutrilog.features.recommendation.model.HealthGoal
import com.example.nutrilog.features.recommendation.model.Recommendation
import com.example.nutrilog.features.recommendation.model.gamification.Achievement
import java.time.LocalDate


/**
 * 适配器接口定义
 */

interface RecordRepository {
    suspend fun getUserRecords(userId: Long, days: Int): List<MealRecord>
    suspend fun getTodayRecords(userId: Long): List<MealRecord>
    suspend fun getStreakDays(userId: Long): Int
    suspend fun getFoodVarietyCount(userId: Long, days: Int): Int
    suspend fun getUserRecordCount(userId: Long): Int  // 添加这个方法
}

interface NutritionAnalysisService {
    suspend fun getNutritionalGaps(userId: Long, days: Int): List<NutritionalGap>
    suspend fun getEatingPatterns(userId: Long): EatingPatternAnalysis
    suspend fun getLatestHealthScore(userId: Long): Double
}



interface GoalRepository {
    suspend fun getActiveGoals(userId: Long): List<HealthGoal>
}

interface RecommendationRepository {
    suspend fun getProcessedRecommendations(userId: Long): List<Long>
    suspend fun saveRecommendation(recommendation: Recommendation)
    suspend fun markAsRead(recommendationId: Long)
    suspend fun markAsApplied(recommendationId: Long)
    suspend fun getTodaysCompletedChallenges(userId: Long, today: LocalDate): List<DailyChallenge>  // 添加这个方法
    suspend fun getUserAchievements(userId: Long): List<Achievement>  // 添加这个方法
    suspend fun updateChallengeProgress(challengeId: Long, progress: Float)  // 添加这个方法
    suspend fun markChallengeCompleted(challengeId: Long)  // 添加这个方法
    suspend fun getRecommendation(recommendationId: Long): Recommendation?  // 添加这个方法
    }

