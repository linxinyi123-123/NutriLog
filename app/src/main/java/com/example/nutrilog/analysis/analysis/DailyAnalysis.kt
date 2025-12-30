package com.example.nutrilog.analysis.analysis

import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.MealRecord
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.NutritionTarget
import com.example.nutrilog.shared.NutritionTargetFactory

data class DailyAnalysis(
    val date: String,
    val nutrition: NutritionFacts,
    val target: NutritionTarget,
    val score: HealthScore,
    val records: List<MealRecord>,
    val mealAnalyses: List<MealAnalysis> = emptyList()
){
    // 👇 新增伴生对象，用于存放工厂方法
    companion object {
        // 👇 定义 empty 函数，返回一个空的 DailyAnalysis
        fun empty(date: String): DailyAnalysis {
            return DailyAnalysis(
                date = date,
                nutrition = NutritionFacts(),
                target = NutritionTargetFactory().createForAdultMale(),
                score = HealthScore(0.0,mapOf<String, Double>(),emptyList()) ,
                records = emptyList()
            )
        }
    }
}