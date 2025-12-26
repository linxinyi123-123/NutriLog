// app/src/main/java/com/nutrilog/features/recommendation/engine/rule/RuleAction.kt
package com.example.nutrilog.features.recommendation.engine.rule

sealed class RuleAction {
    data class SuggestFoods(
        val foodCategories: List<String>,
        val reason: String,
        val mealType: String? = null
    ) : RuleAction()

    data class SuggestHabit(
        val habit: String,
        val frequency: String,
        val duration: Int = 7 // 持续天数
    ) : RuleAction()

    data class ShowEducationalTip(
        val tipId: Long,
        val category: String
    ) : RuleAction()

    data class CreateMealPlan(
        val planType: String,
        val duration: Int, // 天数
        val targetCalories: Int? = null
    ) : RuleAction()

    data class UpdateGoal(
        val goalId: Long,
        val adjustment: Double
    ) : RuleAction()
}