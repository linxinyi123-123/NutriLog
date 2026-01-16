// features/recommendation/adapters/ProviderAdapters.kt
package com.example.nutrilog.features.recommendation.adapters

import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.features.recommendation.model.*
import com.example.nutrilog.features.recommendation.interfaces.*
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * RecordProvider适配器
 */
class RecordProviderAdapter(
    private val recordProvider: RecordProvider
) : RecordRepository {

    override suspend fun getUserRecords(userId: Long, days: Int): List<MealRecord> {
        return recordProvider.getUserRecords(userId, days)
    }

    override suspend fun getTodayRecords(userId: Long): List<MealRecord> {
        return recordProvider.getTodayRecords(userId)
    }

    override suspend fun getStreakDays(userId: Long): Int {
        return recordProvider.getStreakDays(userId)
    }

    override suspend fun getFoodVarietyCount(userId: Long, days: Int): Int {
        return recordProvider.getFoodVarietyCount(userId, days)
    }
}

/**
 * NutritionProvider适配器
 */
class NutritionProviderAdapter(
    private val nutritionProvider: NutritionProvider
) : NutritionAnalysisService {

    override suspend fun getNutritionalGaps(userId: Long, days: Int): List<NutritionalGap> {
        return nutritionProvider.getNutritionalGaps(userId, days)
    }

    override suspend fun getEatingPatterns(userId: Long): EatingPatternAnalysis {
        return nutritionProvider.getEatingPatterns(userId)
    }

    override suspend fun getLatestHealthScore(userId: Long): Double {
        return nutritionProvider.getLatestHealthScore(userId).toDouble()
    }
}



/**
 * Mock目标适配器
 */
class MockGoalAdapter : GoalRepository {
    override suspend fun getActiveGoals(userId: Long): List<HealthGoal> {
        delay(100)
        return listOf(
            HealthGoal(
                id = 1,
                userId = userId,
                type = GoalType.WEIGHT_LOSS,
                target = GoalTarget(value = 65.0, unit = "kg"),
                startDate = "2024-01-01",
                endDate = "2024-06-30",
                progress = 0.6f,
                status = GoalStatus.ACTIVE,
                milestones = listOf(
                    Milestone(
                        id = 1,
                        targetValue = 5.0,
                        rewardPoints = 100,
                        achieved = true,
                        achievedAt = 1704067200000
                    ),
                    Milestone(
                        id = 2,
                        targetValue = 10.0,
                        rewardPoints = 200,
                        achieved = false,
                        achievedAt = null
                    )
                )
            ),
            HealthGoal(
                id = 2,
                userId = userId,
                type = GoalType.HEALTH_IMPROVEMENT,
                target = GoalTarget(value = 80.0, unit = "健康评分"),
                startDate = "2024-01-01",
                endDate = "2024-12-31",
                progress = 0.4f,
                status = GoalStatus.ACTIVE,
                milestones = listOf(
                    Milestone(
                        id = 3,
                        targetValue = 60.0,
                        rewardPoints = 50,
                        achieved = true,
                        achievedAt = 1704067200000
                    ),
                    Milestone(
                        id = 4,
                        targetValue = 70.0,
                        rewardPoints = 100,
                        achieved = false,
                        achievedAt = null
                    ),
                    Milestone(
                        id = 5,
                        targetValue = 80.0,
                        rewardPoints = 200,
                        achieved = false,
                        achievedAt = null
                    )
                )
            )
        )
    }
}