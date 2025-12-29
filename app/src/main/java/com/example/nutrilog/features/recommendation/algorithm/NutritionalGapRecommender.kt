package com.example.nutrilog.features.recommendation.algorithm

import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.interfaces.NutritionalGap
import com.example.nutrilog.features.recommendation.interfaces.Severity
import com.example.nutrilog.features.recommendation.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 基于营养缺口的推荐器
 */
class NutritionalGapRecommender : BaseRecommender() {

    /**
     * 生成基于营养缺口的推荐
     */
    fun generateRecommendations(
        gaps: List<NutritionalGap>,
        context: RecommendationContext
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 只处理中度和严重缺口
        val significantGaps = gaps.filter {
            it.severity == com.example.nutrilog.features.recommendation.interfaces.Severity.SEVERE ||
                    it.severity == com.example.nutrilog.features.recommendation.interfaces.Severity.MODERATE
        }

        significantGaps.forEach { gap ->
            val recommendation = createNutritionalGapRecommendation(gap, context)
            recommendations.add(recommendation)
        }

        // 如果没有显著缺口，生成正面反馈
        if (significantGaps.isEmpty() && gaps.isNotEmpty()) {
            val positiveRecommendation = createPositiveFeedbackRecommendation(context)
            recommendations.add(positiveRecommendation)
        }

        return sortRecommendations(recommendations)
    }

    /**
     * 创建单个营养缺口推荐
     */
    private fun createNutritionalGapRecommendation(
        gap: NutritionalGap,
        context: RecommendationContext
    ): Recommendation {
        // 根据营养素类型生成不同的推荐内容
        val (title, description, actions) = when (gap.nutrient.toLowerCase()) {
            "protein", "蛋白质" -> createProteinRecommendation(gap, context)
            "fiber", "纤维", "膳食纤维" -> createFiberRecommendation(gap, context)
            "iron", "铁" -> createIronRecommendation(gap, context)
            "calcium", "钙" -> createCalciumRecommendation(gap, context)
            "vitamin_c", "维生素c", "vc" -> createVitaminCRecommendation(gap, context)
            else -> createGeneralRecommendation(gap, context)
        }

        return Recommendation(
            id = generateRecommendationId(),
            type = RecommendationType.NUTRITION_GAP,
            title = title,
            description = description,
            priority = calculatePriority(gap.severity),
            confidence = calculateConfidence(gap.severity),
            reason = getReasonTemplate(7), // 假设分析最近7天
            actions = actions,
            metadata = mapOf(
                "nutrient" to gap.nutrient,
                "gapPercentage" to gap.gapPercentage,
                "severity" to gap.severity.name,
                "averageIntake" to gap.averageIntake,
                "recommended" to gap.recommended
            )
        )
    }

    /**
     * 蛋白质相关推荐
     */
    private fun createProteinRecommendation(
        gap: NutritionalGap,
        context: RecommendationContext
    ): Triple<String, String, List<Action>> {
        val title = "蛋白质摄入不足"
        val description = "检测到蛋白质摄入低于推荐值${gap.gapPercentage.toInt()}%。建议增加蛋白质食物摄入，如：\n" +
                "• 鸡胸肉、鱼肉、瘦牛肉\n" +
                "• 鸡蛋、牛奶、酸奶\n" +
                "• 豆腐、豆浆、豆制品"

        val actions = listOf(
            Action.ShowFoodDetails(-1), // -1表示蛋白质类别
            Action.AddToMealPlan(listOf(-1L, -2L, -3L)), // 示例食物ID
            Action.DismissRecommendation("已了解")
        )

        return Triple(title, description, actions)
    }

    /**
     * 膳食纤维相关推荐
     */
    private fun createFiberRecommendation(
        gap: NutritionalGap,
        context: RecommendationContext
    ): Triple<String, String, List<Action>> {
        val title = "膳食纤维摄入不足"
        val description = "检测到膳食纤维摄入低于推荐值${gap.gapPercentage.toInt()}%。建议增加：\n" +
                "• 蔬菜：西兰花、菠菜、胡萝卜\n" +
                "• 水果：苹果、香蕉、梨\n" +
                "• 全谷物：燕麦、糙米、全麦面包"

        val actions = listOf(
            Action.ShowFoodDetails(-2), // -2表示纤维类别
            Action.AddToMealPlan(listOf(-4L, -5L, -6L)),
            Action.DismissRecommendation("已了解")
        )

        return Triple(title, description, actions)
    }

    /**
     * 铁相关推荐
     */
    private fun createIronRecommendation(
        gap: NutritionalGap,
        context: RecommendationContext
    ): Triple<String, String, List<Action>> {
        val title = "铁摄入不足"
        val description = "检测到铁摄入低于推荐值${gap.gapPercentage.toInt()}%。建议增加：\n" +
                "• 红肉：牛肉、羊肉\n" +
                "• 动物肝脏\n" +
                "• 菠菜、豆类、坚果"

        val actions = listOf(
            Action.ShowFoodDetails(-3),
            Action.AddToMealPlan(listOf(-7L, -8L, -9L)),
            Action.DismissRecommendation("已了解")
        )

        return Triple(title, description, actions)
    }

    /**
     * 钙相关推荐
     */
    private fun createCalciumRecommendation(
        gap: NutritionalGap,
        context: RecommendationContext
    ): Triple<String, String, List<Action>> {
        val title = "钙摄入不足"
        val description = "检测到钙摄入低于推荐值${gap.gapPercentage.toInt()}%。建议增加：\n" +
                "• 奶制品：牛奶、酸奶、奶酪\n" +
                "• 豆制品：豆腐、豆浆\n" +
                "• 绿叶蔬菜：菠菜、小白菜"

        val actions = listOf(
            Action.ShowFoodDetails(-4),
            Action.AddToMealPlan(listOf(-10L, -11L, -12L)),
            Action.DismissRecommendation("已了解")
        )

        return Triple(title, description, actions)
    }

    /**
     * 维生素C相关推荐
     */
    private fun createVitaminCRecommendation(
        gap: NutritionalGap,
        context: RecommendationContext
    ): Triple<String, String, List<Action>> {
        val title = "维生素C摄入不足"
        val description = "检测到维生素C摄入低于推荐值${gap.gapPercentage.toInt()}%。建议增加：\n" +
                "• 水果：橙子、猕猴桃、草莓\n" +
                "• 蔬菜：青椒、西兰花、番茄"

        val actions = listOf(
            Action.ShowFoodDetails(-5),
            Action.AddToMealPlan(listOf(-13L, -14L, -15L)),
            Action.DismissRecommendation("已了解")
        )

        return Triple(title, description, actions)
    }

    /**
     * 通用营养素推荐
     */
    private fun createGeneralRecommendation(
        gap: NutritionalGap,
        context: RecommendationContext
    ): Triple<String, String, List<Action>> {
        val title = "${gap.nutrient}摄入不足"
        val description = "检测到${gap.nutrient}摄入低于推荐值${gap.gapPercentage.toInt()}%，建议增加相关食物摄入。"

        val actions = listOf(
            Action.ShowFoodDetails(-100),
            Action.DismissRecommendation("已了解")
        )

        return Triple(title, description, actions)
    }

    /**
     * 创建正面反馈推荐（没有显著缺口时）
     */
    private fun createPositiveFeedbackRecommendation(
        context: RecommendationContext
    ): Recommendation {
        return Recommendation(
            id = generateRecommendationId(),
            type = RecommendationType.EDUCATIONAL,
            title = "营养均衡良好",
            description = "很棒！当前营养摄入比较均衡，继续保持。\n" +
                    "建议每天保持食物多样性，摄入不同种类的食物。",
            priority = Priority.LOW,
            confidence = 0.8f,
            reason = "基于最近7天的营养分析",
            actions = listOf(
                Action.DismissRecommendation("知道了")
            ),
            metadata = mapOf(
                "type" to "positive_feedback",
                "date" to SimpleDateFormat("yyyy-MM-dd").format(Date())
            )
        )
    }
}