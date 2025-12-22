package com.nutrilog.data.entities

data class NutritionFacts(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double? = null,
    val sugar: Double? = null,
    val sodium: Double? = null
) {
    operator fun plus(other: NutritionFacts): NutritionFacts {
        return NutritionFacts(
            calories = this.calories + other.calories,
            protein = this.protein + other.protein,
            carbs = this.carbs + other.carbs,
            fat = this.fat + other.fat,
            fiber = (this.fiber ?: 0.0) + (other.fiber ?: 0.0),
            sugar = (this.sugar ?: 0.0) + (other.sugar ?: 0.0),
            sodium = (this.sodium ?: 0.0) + (other.sodium ?: 0.0)
        )
    }
}