package com.nutrilog.common.models

import java.time.LocalDateTime

data class MealRecord(
    val id: Long = 0,
    val foods: List<QuantifiedFood>,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val mealType: MealType = MealType.OTHER,
    val location: String = "",
    val mood: Int = 3, // 1-5
    val tags: List<String> = emptyList(),
    val notes: String = ""
)

enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK, OTHER
}