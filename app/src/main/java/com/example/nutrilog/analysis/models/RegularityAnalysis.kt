package com.example.nutrilog.analysis.models

data class RegularityAnalysis(
    val breakfastScore: Double,      // 早餐规律性得分
    val lunchScore: Double,          // 午餐规律性得分
    val dinnerScore: Double,         // 晚餐规律性得分
    val lateNightFrequency: Double,  // 夜宵频率
    val suggestions: List<String>    // 改进建议
)

data class TimeRegularity(
    val score: Double,
    val times: List<String>
)