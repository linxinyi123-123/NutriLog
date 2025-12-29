package com.example.nutrilog.analysis.models

// 暂定
data class MealAnalysis(
    val id: Long,              // 餐的唯一标识（可选）
    val mealName: String,      // 餐的名称，比如“早餐”“午餐”
    val totalCalories: Int,    // 这一餐总热量（大卡）
    val proteinRatio: Double,  // 蛋白质占比（0~1 之间）
    val fatRatio: Double,      // 脂肪占比（0~1 之间）
    val carbRatio: Double      // 碳水占比（0~1 之间）
)