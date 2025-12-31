package com.example.nutrilog.features.recommendation.algorithm

import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.model.*

/**
 * 上下文感知推荐器（整合时间和地点）
 */
class ContextAwareRecommender(
    private val timeRecommender: TimeBasedRecommender = TimeBasedRecommender(),
    private val locationRecommender: LocationBasedRecommender = LocationBasedRecommender()
) : BaseRecommender() {

    /**
     * 生成基于上下文的综合推荐
     */
    fun generateContextRecommendations(context: RecommendationContext): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 1. 时间相关的推荐
        val timeRecommendations = timeRecommender.generateTimeRecommendations(context)
        recommendations.addAll(timeRecommendations)

        // 2. 地点相关的推荐
        val locationRecommendations = locationRecommender.generateLocationRecommendations(context)
        recommendations.addAll(locationRecommendations)

        // 3. 时间和地点结合的推荐
        val combinedRecommendations = generateCombinedRecommendations(context)
        recommendations.addAll(combinedRecommendations)

        // 4. 基于场景的特殊推荐
        val specialRecommendations = generateSpecialScenarioRecommendations(context)
        recommendations.addAll(specialRecommendations)

        return deduplicateRecommendations(sortRecommendations(recommendations))
    }

    /**
     * 生成时间和地点结合的综合推荐
     */
    private fun generateCombinedRecommendations(context: RecommendationContext): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        val location = context.location?.toLowerCase() ?: ""
        val mealType = context.mealType ?: ""
        val currentHour = context.currentHour

        // 根据时间和地点的组合生成推荐
        when {
            // 早餐 + 自制
            currentHour in 6..9 && location.contains("自制") -> {
                recommendations.add(
                    Recommendation(
                        id = generateRecommendationId(),
                        type = RecommendationType.MEAL_PLAN,
                        title = "自制早餐搭配",
                        description = "自制早餐建议：\n" +
                                "• 燕麦粥 + 水果\n" +
                                "• 全麦面包 + 鸡蛋 + 牛奶\n" +
                                "• 豆浆 + 包子 + 小菜",
                        priority = Priority.MEDIUM,
                        confidence = 0.8f,
                        reason = "基于早餐时间和自制场景",
                        actions = listOf(
                            Action.ShowFoodDetails(-211),
                            Action.AddToMealPlan(listOf(-60L, -61L, -62L)),
                            Action.DismissRecommendation("不需要")
                        )
                    )
                )
            }

            // 午餐 + 食堂
            currentHour in 11..13 && (location.contains("食堂") || location.contains("cafeteria")) -> {
                recommendations.add(
                    Recommendation(
                        id = generateRecommendationId(),
                        type = RecommendationType.FOOD_SUGGESTION,
                        title = "食堂午餐优化",
                        description = "食堂午餐搭配技巧：\n" +
                                "• 选择不同颜色的蔬菜\n" +
                                "• 蛋白质和主食合理搭配\n" +
                                "• 避免汤汁泡饭\n" +
                                "• 饭后适量水果",
                        priority = Priority.MEDIUM,
                        confidence = 0.75f,
                        reason = "基于午餐时间和食堂场景",
                        actions = listOf(
                            Action.ShowFoodDetails(-212),
                            Action.DismissRecommendation("了解")
                        )
                    )
                )
            }

            // 晚餐 + 外卖
            currentHour in 18..20 && location.contains("外卖") -> {
                recommendations.add(
                    Recommendation(
                        id = generateRecommendationId(),
                        type = RecommendationType.HABIT_IMPROVEMENT,
                        title = "晚餐外卖健康点",
                        description = "晚餐外卖健康选择：\n" +
                                "• 选择清淡的烹饪方式\n" +
                                "• 控制主食分量\n" +
                                "• 增加蔬菜比例\n" +
                                "• 避免高油高盐",
                        priority = Priority.MEDIUM,
                        confidence = 0.7f,
                        reason = "基于晚餐时间和外卖场景",
                        actions = listOf(
                            Action.ShowFoodDetails(-213),
                            Action.DismissRecommendation("知道了")
                        )
                    )
                )
            }

            // 忙碌时间 + 快餐
            (currentHour in 7..9 || currentHour in 11..13 || currentHour in 18..19) &&
                    location.contains("快餐") -> {
                recommendations.add(
                    Recommendation(
                        id = generateRecommendationId(),
                        type = RecommendationType.FOOD_SUGGESTION,
                        title = "忙碌时快速营养餐",
                        description = "忙碌时快餐选择：\n" +
                                "• 选择烤鸡三明治\n" +
                                "• 搭配蔬菜沙拉\n" +
                                "• 选择水或茶\n" +
                                "• 避免套餐升级",
                        priority = Priority.MEDIUM,
                        confidence = 0.65f,
                        reason = "基于忙碌时段和快餐场景",
                        actions = listOf(
                            Action.ShowFoodDetails(-214),
                            Action.AddToMealPlan(listOf(-63L, -64L, -65L)),
                            Action.DismissRecommendation("不需要")
                        )
                    )
                )
            }
        }

        return recommendations
    }

    /**
     * 生成特殊场景推荐
     */
    private fun generateSpecialScenarioRecommendations(context: RecommendationContext): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 检查是否为第一次使用
        if (context.isFirstTimeUser) {
            recommendations.add(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.EDUCATIONAL,
                    title = "欢迎使用场景化推荐",
                    description = "根据您的时间和地点，我们会提供个性化的饮食建议。\n" +
                            "请允许位置权限和时间同步，以获得更准确的推荐。",
                    priority = Priority.HIGH,
                    confidence = 1.0f,
                    reason = "新用户引导",
                    actions = listOf(
                        Action.DismissRecommendation("知道了")
                    )
                )
            )
        }

        // 检查饮食限制
        val dietaryRestrictions = context.dietaryRestrictions ?: emptyList()
        if (dietaryRestrictions.isNotEmpty()) {
            val restrictionStr = dietaryRestrictions.joinToString("、")
            recommendations.add(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.HABIT_IMPROVEMENT,
                    title = "饮食限制提醒",
                    description = "根据您的饮食限制($restrictionStr)，在外用餐时请注意：\n" +
                            "• 提前告知餐厅\n" +
                            "• 仔细查看菜单说明\n" +
                            "• 询问食材和烹饪方式",
                    priority = Priority.MEDIUM,
                    confidence = 0.9f,
                    reason = "基于您的饮食限制",
                    actions = listOf(
                        Action.DismissRecommendation("了解")
                    ),
                    metadata = mapOf("dietaryRestrictions" to dietaryRestrictions)
                )
            )
        }

        // 检查预算
        val budgetRange = context.budgetRange
        if (budgetRange != null && budgetRange.max < 30.0) {
            recommendations.add(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.FOOD_SUGGESTION,
                    title = "经济型用餐建议",
                    description = "根据您的预算(${budgetRange.min}-${budgetRange.max}元)，建议：\n" +
                            "• 食堂或自制更经济\n" +
                            "• 关注外卖平台的优惠\n" +
                            "• 适量囤积食材\n" +
                            "• 避免频繁外食",
                    priority = Priority.LOW,
                    confidence = 0.6f,
                    reason = "基于您的预算范围",
                    actions = listOf(
                        Action.ShowFoodDetails(-215),
                        Action.DismissRecommendation("不需要")
                    )
                )
            )
        }

        return recommendations
    }

    /**
     * 获取当前场景描述
     */
    fun getCurrentScenario(context: RecommendationContext): String {
        val timeDesc = when (context.currentHour) {
            in 5..10 -> "早晨"
            in 11..14 -> "中午"
            in 17..21 -> "晚上"
            else -> "其他时间"
        }

        val locationDesc = when (context.location?.toLowerCase()) {
            "食堂", "cafeteria" -> "在食堂"
            "外卖", "delivery" -> "点外卖"
            "餐厅", "restaurant" -> "在餐厅"
            "自制", "home", "home cooking" -> "自制"
            else -> "在外"
        }

        return "${timeDesc} ${locationDesc}用餐"

    }
}