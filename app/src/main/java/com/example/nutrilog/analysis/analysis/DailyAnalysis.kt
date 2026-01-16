package com.example.nutrilog.analysis.analysis

import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.MealRecord
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.NutritionTarget
import com.example.nutrilog.shared.NutritionTargetFactory
import com.example.nutrilog.shared.Range

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

    fun toSharedAnalysis(analysis:DailyAnalysis): com.example.nutrilog.shared.DailyAnalysis
    {
        return com.example.nutrilog.shared.DailyAnalysis(
            date = analysis.date,
            score = analysis.score,
            nutrition = analysis.nutrition,
            target = NutritionFacts(
                calories = analysis.target.calories.min,
                protein = analysis.target.protein.min,
                carbs = analysis.target.carbs.min,
                fat = analysis.target.fat.min,
                sodium = analysis.target.sodium,                // 5克盐约等于2000mg钠
                fiber = analysis.target.fiber,                    // 每日25克膳食纤维
                sugar = analysis.target.sugar                   // 每日不超过50克添加糖
            ),
            records = analysis.records,
        )
    }
}