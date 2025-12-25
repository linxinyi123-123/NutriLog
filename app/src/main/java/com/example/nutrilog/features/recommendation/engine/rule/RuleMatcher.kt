// app/src/main/java/com/nutrilog/features/recommendation/engine/rule/RuleMatcher.kt
package com.example.nutrilog.features.recommendation.engine.rule

import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.interfaces.Severity
import timber.log.Timber

class RuleMatcher {

    /**
     * 检查规则是否匹配当前上下文
     */
    fun matchRule(
        rule: RecommendationRule,
        context: RecommendationContext
    ): Boolean {
        if (!rule.isActive) return false

        // 检查冷却时间
        if (rule.cooldown > 0) {
            // 这里需要存储上次触发时间，简化起见先跳过
        }

        // 检查过期时间
        rule.expiration?.let {
            if (System.currentTimeMillis() > it) return false
        }

        return evaluateCondition(rule.condition, context)
    }

    /**
     * 评估条件是否满足
     */
    private fun evaluateCondition(
        condition: RuleCondition,
        context: RecommendationContext
    ): Boolean {
        return try {
            when (condition) {
                is RuleCondition.NutrientGap -> {
                    evaluateNutrientGap(condition, context.nutritionalGaps)
                }
                is RuleCondition.MealPattern -> {
                    evaluateMealPattern(condition, context.mealPatterns)
                }
                is RuleCondition.HealthScore -> {
                    evaluateHealthScore(condition, context.healthScore)
                }
                is RuleCondition.GoalProgress -> {
                    evaluateGoalProgress(condition, context.healthGoals)
                }
                is RuleCondition.CompositeCondition -> {
                    evaluateCompositeCondition(condition, context)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "规则条件评估失败")
            false
        }
    }

    /**
     * 评估营养缺口条件
     */
    private fun evaluateNutrientGap(
        condition: RuleCondition.NutrientGap,
        gaps: List<com.example.nutrilog.features.recommendation.interfaces.NutritionalGap>
    ): Boolean {
        val gap = gaps.find { it.nutrient == condition.nutrient }
        return when {
            gap == null -> false
            condition.comparison == Comparison.GREATER_THAN ->
                gap.gapPercentage > condition.threshold
            condition.comparison == Comparison.LESS_THAN ->
                gap.gapPercentage < condition.threshold
            condition.comparison == Comparison.GREATER_THAN_OR_EQUAL ->
                gap.gapPercentage >= condition.threshold
            condition.comparison == Comparison.LESS_THAN_OR_EQUAL ->
                gap.gapPercentage <= condition.threshold
            condition.comparison == Comparison.EQUALS ->
                gap.gapPercentage == condition.threshold
            condition.comparison == Comparison.NOT_EQUALS ->
                gap.gapPercentage != condition.threshold
            else -> false
        }
    }

    /**
     * 评估饮食模式条件
     */
    private fun evaluateMealPattern(
        condition: RuleCondition.MealPattern,
        patterns: com.example.nutrilog.features.recommendation.interfaces.EatingPatternAnalysis?
    ): Boolean {
        if (patterns == null) return false

        return when (condition.pattern) {
            "meal_regularity" -> {
                patterns.mealRegularity >= condition.frequency
            }
            "food_variety" -> {
                patterns.foodVariety >= condition.frequency
            }
            else -> {
                // 检查不健康模式
                patterns.unhealthyPatterns.contains(condition.pattern)
            }
        }
    }

    /**
     * 评估健康评分条件
     */
    private fun evaluateHealthScore(
        condition: RuleCondition.HealthScore,
        healthScore: Int
    ): Boolean {
        return when (condition.comparison) {
            Comparison.GREATER_THAN -> healthScore > condition.score
            Comparison.LESS_THAN -> healthScore < condition.score
            Comparison.GREATER_THAN_OR_EQUAL -> healthScore >= condition.score
            Comparison.LESS_THAN_OR_EQUAL -> healthScore <= condition.score
            Comparison.EQUALS -> healthScore == condition.score
            Comparison.NOT_EQUALS -> healthScore != condition.score
        }
    }

    /**
     * 评估目标进度条件
     */
    private fun evaluateGoalProgress(
        condition: RuleCondition.GoalProgress,
        goals: List<com.example.nutrilog.features.recommendation.model.HealthGoal>
    ): Boolean {
        val goal = goals.find { it.type.name == condition.goalType }
        return when {
            goal == null -> false
            condition.comparison == Comparison.GREATER_THAN ->
                goal.progress > condition.progress
            condition.comparison == Comparison.LESS_THAN ->
                goal.progress < condition.progress
            condition.comparison == Comparison.GREATER_THAN_OR_EQUAL ->
                goal.progress >= condition.progress
            condition.comparison == Comparison.LESS_THAN_OR_EQUAL ->
                goal.progress <= condition.progress
            condition.comparison == Comparison.EQUALS ->
                goal.progress == condition.progress
            condition.comparison == Comparison.NOT_EQUALS ->
                goal.progress != condition.progress
            else -> false
        }
    }

    /**
     * 评估复合条件
     */
    private fun evaluateCompositeCondition(
        condition: RuleCondition.CompositeCondition,
        context: RecommendationContext
    ): Boolean {
        val results = condition.conditions.map { evaluateCondition(it, context) }

        return when (condition.operator) {
            LogicalOperator.AND -> results.all { it }
            LogicalOperator.OR -> results.any { it }
        }
    }

    /**
     * 批量匹配规则
     */
    fun matchRules(
        rules: List<RecommendationRule>,
        context: RecommendationContext
    ): List<RecommendationRule> {
        return rules.filter { matchRule(it, context) }
    }

    /**
     * 计算规则的匹配置信度
     */
    fun calculateConfidence(
        rule: RecommendationRule,
        context: RecommendationContext
    ): Float {
        if (!matchRule(rule, context)) return 0f

        return when (rule.condition) {
            is RuleCondition.NutrientGap -> {
                calculateNutrientGapConfidence(rule.condition, context.nutritionalGaps)
            }
            is RuleCondition.MealPattern -> {
                calculateMealPatternConfidence(rule.condition, context.mealPatterns)
            }
            is RuleCondition.HealthScore -> {
                calculateHealthScoreConfidence(rule.condition, context.healthScore)
            }
            is RuleCondition.GoalProgress -> {
                calculateGoalProgressConfidence(rule.condition, context.healthGoals)
            }
            is RuleCondition.CompositeCondition -> {
                calculateCompositeConfidence(rule.condition, context)
            }
        }
    }

    private fun calculateNutrientGapConfidence(
        condition: RuleCondition.NutrientGap,
        gaps: List<com.example.nutrilog.features.recommendation.interfaces.NutritionalGap>
    ): Float {
        val gap = gaps.find { it.nutrient == condition.nutrient } ?: return 0f

        // 根据严重程度计算置信度
        return when (gap.severity) {
            Severity.SEVERE -> 0.9f
            Severity.MODERATE -> 0.7f
            Severity.MILD -> 0.5f
        }
    }

    private fun calculateMealPatternConfidence(
        condition: RuleCondition.MealPattern,
        patterns: com.example.nutrilog.features.recommendation.interfaces.EatingPatternAnalysis?
    ): Float {
        return 0.6f // 简化版本
    }

    private fun calculateHealthScoreConfidence(
        condition: RuleCondition.HealthScore,
        healthScore: Int
    ): Float {
        // 分数偏离基准值越大，置信度越高
        val deviation = Math.abs(healthScore - condition.score) / 100.0f
        return 0.5f + deviation * 0.5f
    }

    private fun calculateGoalProgressConfidence(
        condition: RuleCondition.GoalProgress,
        goals: List<com.example.nutrilog.features.recommendation.model.HealthGoal>
    ): Float {
        val goal = goals.find { it.type.name == condition.goalType } ?: return 0f
        return goal.progress
    }

    private fun calculateCompositeConfidence(
        condition: RuleCondition.CompositeCondition,
        context: RecommendationContext
    ): Float {
        val confidences = condition.conditions.map { calculateSubConditionConfidence(it, context) }

        return when (condition.operator) {
            LogicalOperator.AND -> confidences.minOrNull() ?: 0f
            LogicalOperator.OR -> confidences.maxOrNull() ?: 0f
        }
    }

    private fun calculateSubConditionConfidence(
        condition: RuleCondition,
        context: RecommendationContext
    ): Float {
        return when (condition) {
            is RuleCondition.NutrientGap ->
                calculateNutrientGapConfidence(condition, context.nutritionalGaps)
            is RuleCondition.MealPattern ->
                calculateMealPatternConfidence(condition, context.mealPatterns)
            is RuleCondition.HealthScore ->
                calculateHealthScoreConfidence(condition, context.healthScore)
            is RuleCondition.GoalProgress ->
                calculateGoalProgressConfidence(condition, context.healthGoals)
            is RuleCondition.CompositeCondition ->
                calculateCompositeConfidence(condition, context)
        }
    }
}