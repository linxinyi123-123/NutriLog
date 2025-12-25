// app/src/main/java/com/nutrilog/features/recommendation/engine/rule/RuleCondition.kt
package com.example.nutrilog.features.recommendation.engine.rule

import com.example.nutrilog.features.recommendation.interfaces.NutritionalGap
import com.example.nutrilog.features.recommendation.interfaces.EatingPatternAnalysis

sealed class RuleCondition {
    data class NutrientGap(
        val nutrient: String,
        val threshold: Double,    // 阈值
        val comparison: Comparison // 比较方式
    ) : RuleCondition()

    data class MealPattern(
        val pattern: String,      // 模式名称
        val frequency: Int       // 频率阈值
    ) : RuleCondition()

    data class HealthScore(
        val score: Int,           // 分数阈值
        val comparison: Comparison
    ) : RuleCondition()

    data class GoalProgress(
        val goalType: String,     // 目标类型
        val progress: Float,      // 进度阈值
        val comparison: Comparison
    ) : RuleCondition()

    data class CompositeCondition(
        val operator: LogicalOperator,
        val conditions: List<RuleCondition>
    ) : RuleCondition()
}