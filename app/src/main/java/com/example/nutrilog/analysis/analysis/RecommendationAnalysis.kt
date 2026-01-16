package com.example.nutrilog.analysis.analysis

import com.example.nutrilog.features.recommendation.interfaces.DailyScore
import com.example.nutrilog.features.recommendation.interfaces.EatingPatternAnalysis

interface RecommendationAnalysis {
    // 识别营养缺口
    suspend fun identifyNutritionalGaps(
        userId: Long,
        days: Int = 7
    ): List<NutritionalGap>

    // 分析饮食习惯模式
    suspend fun analyzeEatingPatterns(
        userId: Long,
        startDate: String,
        endDate: String
    ): EatingPatternAnalysis

    // 获取饮食评分历史
    suspend fun getHealthScoreHistory(
        userId: Long,
        days: Int
    ): List<DailyScore>
}

data class NutritionalGap(
    val nutrient: String,      // 营养素名称
    val averageIntake: Double, // 平均摄入量
    val recommended: Double,   // 推荐量
    val gapPercentage: Double, // 缺口百分比
    val severity: Severity     // 严重程度
)

enum class Severity {
    MILD,      // 轻微缺口 (<20%)
    MODERATE,  // 中度缺口 (20%-50%)
    SEVERE     // 严重缺口 (>50%)
}