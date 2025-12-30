package com.example.nutrilog.analysis.calculator

import com.example.nutrilog.shared.FoodItem
import com.example.nutrilog.shared.NutritionFacts

class OptimizedNutritionCalculator {
    // 使用缓存避免重复计算
    private val foodNutritionCache = mutableMapOf<Pair<Long, Double>, NutritionFacts>()

    fun calculateFoodNutrition(food: FoodItem, amountGrams: Double): NutritionFacts {
        val cacheKey = food.id to amountGrams

        return foodNutritionCache.getOrPut(cacheKey) {
            // 原始计算逻辑
            val ratio = amountGrams / 100.0
            NutritionFacts(
                calories = food.calories * ratio,
                protein = food.protein * ratio,
                carbs = food.carbs * ratio,
                fat = food.fat * ratio,
                sodium = food.sodium?.let { it * ratio },
                fiber = food.fiber?.let { it * ratio },
                sugar = food.sugar?.let { it * ratio }
            )
        }
    }
}