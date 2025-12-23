package com.example.nutrilog.analysis.calculator

import com.example.nutrilog.data.entities.NutritionFacts
import com.example.nutrilog.shared.FoodItem
import com.example.nutrilog.shared.MealRecord

class BasicNutritionCalculator {
    // 计算单个食物的营养
    fun calculateFoodNutrition(food: FoodItem, amountGrams: Double): NutritionFacts {
        val ratio = amountGrams / 100.0
        return NutritionFacts(
            calories = food.calories * ratio,
            protein = food.protein * ratio,
            carbs = food.carbs * ratio,
            fat = food.fat * ratio,
            sodium = food.sodium?.times(ratio),
            fiber = food.fiber?.times(ratio),
            sugar = food.sugar?.times(ratio)
        )
    }

    // 计算一餐的营养总和
    fun calculateMealNutrition(foods: List<Pair<FoodItem, Double>>): NutritionFacts {
        val total = NutritionFacts()
        foods.forEach { (food, amount) ->
            total.plus(calculateFoodNutrition(food, amount))
        }
        return total
    }

    // 计算一天的营养总和
    fun calculateDailyNutrition(meals: List<MealRecord>): NutritionFacts {
        val total = NutritionFacts()
        meals.forEach { meal ->
            total.plus(calculateMealNutrition(meal.foods))
        }
        return total
    }
}