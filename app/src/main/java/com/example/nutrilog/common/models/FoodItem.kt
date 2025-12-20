package com.nutrilog.common.models

data class FoodItem(
    val id: Long = 0,
    val name: String,
    val category: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val imageUrl: String = ""
)

data class QuantifiedFood(
    val food: FoodItem,
    val quantity: Double,
    val unit: String // "g", "ml", "个", "碗"
)