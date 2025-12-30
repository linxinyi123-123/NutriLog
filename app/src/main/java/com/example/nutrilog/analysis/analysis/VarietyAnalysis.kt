package com.example.nutrilog.analysis.analysis

import com.example.nutrilog.shared.FoodCategory

data class VarietyAnalysis(
    val totalScore: Double,  // 多样性总分(0-100)
    val coverage: Map<FoodCategory, Double>,  // 各类别覆盖率百分比
    val suggestions: List<String>  // 改进建议
)

data class DailyVariety(
    val date: String,
    val categories: Set<FoodCategory>,  // 当天覆盖的类别
    val score: Double                   // 当天多样性得分
)