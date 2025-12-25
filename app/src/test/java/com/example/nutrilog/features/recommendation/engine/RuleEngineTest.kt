// app/src/test/java/com/nutrilog/features/recommendation/engine/RuleEngineTest.kt
package com.nutrilog.features.recommendation.engine

import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.engine.rule.*
import com.example.nutrilog.features.recommendation.interfaces.NutritionalGap
import com.example.nutrilog.features.recommendation.interfaces.Severity
import com.example.nutrilog.features.recommendation.model.Priority
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RuleEngineTest {

    private lateinit var ruleMatcher: RuleMatcher
    private lateinit var testContext: RecommendationContext

    @Before
    fun setup() {
        ruleMatcher = RuleMatcher()

        // 创建测试上下文
        testContext = RecommendationContext(
            userId = 1,
            nutritionalGaps = listOf(
                NutritionalGap(
                    nutrient = "protein",
                    averageIntake = 40.0,
                    recommended = 70.0,
                    gapPercentage = 42.9,
                    severity = Severity.SEVERE
                ),
                NutritionalGap(
                    nutrient = "fiber",
                    averageIntake = 15.0,
                    recommended = 25.0,
                    gapPercentage = 40.0,
                    severity = Severity.MODERATE
                )
            ),
            healthScore = 55,
            healthGoals = emptyList()
        )
    }

    @Test
    fun testNutrientGapRuleMatch() {
        // 创建规则：蛋白质缺口大于30%
        val rule = RecommendationRule(
            id = 1,
            name = "蛋白质补充规则",
            type = RuleType.NUTRITION_GAP,
            condition = RuleCondition.NutrientGap(
                nutrient = "protein",
                threshold = 30.0,
                comparison = Comparison.GREATER_THAN
            ),
            action = RuleAction.SuggestFoods(
                foodCategories = listOf("高蛋白"),
                reason = "蛋白质补充"
            ),
            priority = Priority.HIGH,
            message = "检测到蛋白质摄入不足"
        )

        assertTrue(ruleMatcher.matchRule(rule, testContext))
    }

    @Test
    fun testNutrientGapRuleNotMatch() {
        // 创建规则：钙缺口大于50%（用户没有钙缺口数据）
        val rule = RecommendationRule(
            id = 2,
            name = "钙补充规则",
            type = RuleType.NUTRITION_GAP,
            condition = RuleCondition.NutrientGap(
                nutrient = "calcium",
                threshold = 50.0,
                comparison = Comparison.GREATER_THAN
            ),
            action = RuleAction.SuggestFoods(
                foodCategories = listOf("高钙"),
                reason = "钙补充"
            ),
            priority = Priority.HIGH,
            message = "检测到钙摄入不足"
        )

        assertFalse(ruleMatcher.matchRule(rule, testContext))
    }

    @Test
    fun testHealthScoreRuleMatch() {
        // 创建规则：健康评分小于60
        val rule = RecommendationRule(
            id = 3,
            name = "健康评分规则",
            type = RuleType.HEALTH_SCORE,
            condition = RuleCondition.HealthScore(
                score = 60,
                comparison = Comparison.LESS_THAN
            ),
            action = RuleAction.ShowEducationalTip(
                tipId = 101,
                category = "营养均衡"
            ),
            priority = Priority.MEDIUM,
            message = "健康评分较低"
        )

        assertTrue(ruleMatcher.matchRule(rule, testContext))
    }

    @Test
    fun testCompositeRuleMatch() {
        // 创建复合规则：蛋白质缺口大于20%且健康评分小于70
        val rule = RecommendationRule(
            id = 4,
            name = "复合规则",
            type = RuleType.COMPOSITE,
            condition = RuleCondition.CompositeCondition(
                operator = LogicalOperator.AND,
                conditions = listOf(
                    RuleCondition.NutrientGap(
                        nutrient = "protein",
                        threshold = 20.0,
                        comparison = Comparison.GREATER_THAN
                    ),
                    RuleCondition.HealthScore(
                        score = 70,
                        comparison = Comparison.LESS_THAN
                    )
                )
            ),
            action = RuleAction.SuggestFoods(
                foodCategories = listOf("高蛋白", "均衡营养"),
                reason = "综合改善"
            ),
            priority = Priority.HIGH,
            message = "蛋白质不足且整体营养需要改善"
        )

        assertTrue(ruleMatcher.matchRule(rule, testContext))
    }

    @Test
    fun testConfidenceCalculation() {
        val rule = RecommendationRule(
            id = 1,
            name = "蛋白质规则",
            type = RuleType.NUTRITION_GAP,
            condition = RuleCondition.NutrientGap(
                nutrient = "protein",
                threshold = 30.0,
                comparison = Comparison.GREATER_THAN
            ),
            action = RuleAction.SuggestFoods(
                foodCategories = listOf("高蛋白"),
                reason = "蛋白质补充"
            ),
            priority = Priority.HIGH,
            message = "检测到蛋白质摄入不足"
        )

        val confidence = ruleMatcher.calculateConfidence(rule, testContext)
        assertTrue(confidence > 0.8f) // 严重不足应该有高置信度
    }

    @Test
    fun testBatchRuleMatching() {
        val rules = listOf(
            RecommendationRule(
                id = 1,
                name = "规则1",
                type = RuleType.NUTRITION_GAP,
                condition = RuleCondition.NutrientGap(
                    nutrient = "protein",
                    threshold = 30.0,
                    comparison = Comparison.GREATER_THAN
                ),
                action = RuleAction.SuggestFoods(
                    foodCategories = listOf("高蛋白"),
                    reason = "蛋白质补充"
                ),
                priority = Priority.HIGH,
                message = "规则1消息"
            ),
            RecommendationRule(
                id = 2,
                name = "规则2",
                type = RuleType.HEALTH_SCORE,
                condition = RuleCondition.HealthScore(
                    score = 80,
                    comparison = Comparison.GREATER_THAN
                ),
                action = RuleAction.ShowEducationalTip(
                    tipId = 102,
                    category = "优秀习惯"
                ),
                priority = Priority.LOW,
                message = "规则2消息"
            )
        )

        val matchedRules = ruleMatcher.matchRules(rules, testContext)
        assertEquals(1, matchedRules.size) // 只有规则1应该匹配
        assertEquals(1, matchedRules[0].id)
    }
}