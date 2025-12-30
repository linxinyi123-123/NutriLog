package com.example.nutrilog.analysis.analysis

import com.example.nutrilog.shared.NutritionFacts

data class TrendAnalysis(
    val period: String,  // week/month
    val startDate: String,
    val endDate: String,
    val dailyPoints: List<DailyTrendPoint>,
    val trends: Map<String, TrendDirection>,
    val insights: List<String>  // 趋势洞察
)

data class DailyTrendPoint(
    val date: String,
    val nutrition: NutritionFacts,
    val score: Double
)

enum class TrendDirection {
    UP,     // 上升趋势
    DOWN,   // 下降趋势
    STABLE  // 稳定趋势
}