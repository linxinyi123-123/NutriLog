package com.example.nutrilog.analysis.analysis

import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.MealRecord
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.NutritionTarget

data class DailyAnalysis(
    val date: String,
    val nutrition: NutritionFacts,
    val target: NutritionTarget,
    val score: HealthScore,
    val records: List<MealRecord>,
    val mealAnalyses: List<MealAnalysis> = emptyList()
)