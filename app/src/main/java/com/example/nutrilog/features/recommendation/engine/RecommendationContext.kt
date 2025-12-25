// app/src/main/java/com/nutrilog/features/recommendation/engine/RecommendationContext.kt
package com.example.nutrilog.features.recommendation.engine

import com.example.nutrilog.features.recommendation.interfaces.NutritionalGap
import com.example.nutrilog.features.recommendation.interfaces.EatingPatternAnalysis
import com.example.nutrilog.features.recommendation.interfaces.DailyScore
import com.example.nutrilog.features.recommendation.model.HealthGoal
import java.time.LocalTime
import java.time.LocalDate

data class RecommendationContext(
    // 用户信息
    val userId: Long,
    val currentTime: Long = System.currentTimeMillis(),

    // 营养分析数据
    val nutritionalGaps: List<NutritionalGap> = emptyList(),
    val mealPatterns: EatingPatternAnalysis? = null,
    val healthScore: Int = 0,
    val healthScoreHistory: List<DailyScore> = emptyList(),

    // 用户状态
    val recentMeals: List<Any> = emptyList(), // 简化版，实际应该是MealRecord
    val healthGoals: List<HealthGoal> = emptyList(),

    // 用户偏好
    val dietaryRestrictions: List<String> = emptyList(),
    val dislikedFoods: List<String> = emptyList(),
    val preferredCuisines: List<String> = emptyList(),
    val cookingTimeAvailability: CookingTime = CookingTime.MODERATE,
    val budgetRange: BudgetRange? = null,

    // 上下文信息
    val location: String? = null,
    val mealType: String? = null,
    val currentHour: Int = LocalTime.now().hour,
    val currentDate: LocalDate = LocalDate.now(),

    // 设备/应用状态
    val isFirstTimeUser: Boolean = false,
    val appUsageCount: Int = 0
)

data class BudgetRange(
    val min: Double,
    val max: Double
)

enum class CookingTime {
    QUICK,      // <15分钟
    MODERATE,   // 15-30分钟
    EXTENDED    // >30分钟
}

data class UserPreferences(
    val dietaryRestrictions: List<String> = emptyList(),
    val dislikedFoods: List<String> = emptyList(),
    val preferredCuisines: List<String> = emptyList(),
    val cookingTimeAvailability: CookingTime = CookingTime.MODERATE,
    val budgetRange: BudgetRange? = null
)