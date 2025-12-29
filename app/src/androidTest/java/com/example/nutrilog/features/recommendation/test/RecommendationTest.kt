package com.example.nutrilog.features.recommendation.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nutrilog.features.recommendation.algorithm.*
import com.example.nutrilog.features.recommendation.factory.RecommendationFactory
import com.example.nutrilog.features.recommendation.model.*
import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.interfaces.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class RecommendationTest {

    @Test
    fun testNutritionalGapRecommender() {
        println("=".repeat(60))
        println("测试营养缺口推荐器")
        println("=".repeat(60))

        val context = createTestRecommendationContext()
        val gapRecommender = NutritionalGapRecommender()
        val gapRecommendations = gapRecommender.generateRecommendations(
            context.nutritionalGaps,
            context
        )

        println("生成${gapRecommendations.size}条营养缺口推荐")

        // 断言至少有一条推荐
        assert(gapRecommendations.isNotEmpty())
    }

    @Test
    fun testGoalBasedRecommender() {
        println("\n测试健康目标推荐器")
        println("-".repeat(40))

        val context = createTestRecommendationContext()
        val goalRecommender = GoalBasedRecommender()
        val goalRecommendations = goalRecommender.generateGoalRecommendations(
            context.healthGoals,
            context
        )

        println("生成${goalRecommendations.size}条健康目标推荐")

        // 断言
        assert(goalRecommendations.isNotEmpty())
    }

    @Test
    fun testRecommendationFactory() = runBlocking {
        println("\n测试推荐工厂")
        println("-".repeat(40))

        val context = createTestRecommendationContext()
        val factory = RecommendationFactory()
        val allRecommendations = factory.generateAllRecommendations(context)

        println("推荐工厂整合生成${allRecommendations.size}条推荐")

        // 按类型统计
        val byType = allRecommendations.groupBy { it.type }
        byType.forEach { (type, recs) ->
            println("  ${type.name}: ${recs.size}条")
        }

        // 断言
        assert(allRecommendations.isNotEmpty())
    }

    // 辅助方法保持不变
    private fun createTestNutritionalGaps(): List<NutritionalGap> {
        return listOf(
            NutritionalGap(
                nutrient = "蛋白质",
                averageIntake = 45.0,
                recommended = 70.0,
                gapPercentage = 35.7,
                severity = Severity.SEVERE
            ),
            NutritionalGap(
                nutrient = "膳食纤维",
                averageIntake = 18.0,
                recommended = 25.0,
                gapPercentage = 28.0,
                severity = Severity.MODERATE
            )
        )
    }

    private fun createTestHealthGoals(): List<HealthGoal> {
        return listOf(
            HealthGoal(
                id = 1,
                userId = 1,
                type = GoalType.WEIGHT_LOSS,
                target = GoalTarget(1800.0, "kcal", 300.0),
                startDate = LocalDate.now().minusDays(7).toString(),
                endDate = LocalDate.now().plusDays(21).toString(),
                progress = 0.3f,
                milestones = listOf(
                    Milestone(1, 900.0, 50),
                    Milestone(2, 1800.0, 100)
                ),
                status = GoalStatus.ACTIVE
            )
        )
    }

    private fun createTestEatingPatterns(): EatingPatternAnalysis {
        return EatingPatternAnalysis(
            mealRegularity = 75.0,
            timeDistribution = mapOf(
                "早餐" to 0.8,
                "午餐" to 0.9,
                "晚餐" to 0.7,
                "加餐" to 0.3
            ),
            foodVariety = 12,
            unhealthyPatterns = listOf("晚餐过晚", "蔬菜不足")
        )
    }

    private fun createTestRecommendationContext(): RecommendationContext {
        return RecommendationContext(
            userId = 1,
            currentTime = System.currentTimeMillis(),
            nutritionalGaps = createTestNutritionalGaps(),
            mealPatterns = createTestEatingPatterns(),
            healthScore = 75,
            healthScoreHistory = listOf(
                DailyScore("2024-01-01", 70, -150.0, 0.7),
                DailyScore("2024-01-02", 72, -100.0, 0.75)
            ),
            recentMeals = emptyList(),
            healthGoals = createTestHealthGoals(),
            dietaryRestrictions = listOf("乳糖不耐受"),
            dislikedFoods = listOf("香菜", "苦瓜"),
            preferredCuisines = listOf("中餐", "日料"),
            cookingTimeAvailability = CookingTime.MODERATE,
            budgetRange = BudgetRange(20.0, 50.0),
            location = "食堂",
            mealType = "午餐",
            currentHour = 12,
            currentDate = LocalDate.now(),
            isFirstTimeUser = false,
            appUsageCount = 15
        )
    }
}