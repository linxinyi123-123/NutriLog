package com.nutrilog.features.recommendation.interfaces

import kotlinx.coroutines.flow.Flow

// 这是需要从同学B获取的数据接口
// 先定义好，等同学B实现后对接

// 营养缺口数据
data class NutritionalGap(
    val nutrient: String,
    val averageIntake: Double,
    val recommended: Double,
    val gapPercentage: Double,
    val severity: Severity
)

enum class Severity {
    MILD,      // 轻度不足 (<20%)
    MODERATE,  // 中度不足 (20-50%)
    SEVERE     // 严重不足 (>50%)
}

// 饮食模式分析
data class EatingPatternAnalysis(
    val mealRegularity: Double,     // 餐次规律性评分 (0-100)
    val timeDistribution: Map<String, Double>, // 时间分布
    val foodVariety: Int,           // 食物种类数量
    val unhealthyPatterns: List<String>
)

// 每日评分
data class DailyScore(
    val date: String,
    val score: Int,
    val calorieBalance: Double,
    val nutritionBalance: Double
)

// 接口定义
interface NutritionAnalysisProvider {
    suspend fun getNutritionalGaps(userId: Long, days: Int): List<NutritionalGap>
    suspend fun getEatingPatterns(userId: Long): EatingPatternAnalysis
    suspend fun getHealthScoreHistory(userId: Long, days: Int): List<DailyScore>
}