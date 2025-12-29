package com.example.nutrilog.analysis.models

data class DailyAnalysis(
    val date: String,
    val nutrition: NutritionFacts,
    val target: NutritionTarget,
    val score: HealthScore,
    val records: List<MealRecord>,
    val mealAnalyses: List<MealAnalysis> = emptyList()
)