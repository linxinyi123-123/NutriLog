package com.example.nutrilog.features.recommendation.algorithm

import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.model.*

/**
 * 基于地点的推荐器
 */
class LocationBasedRecommender : BaseRecommender() {

    /**
     * 生成基于地点的推荐
     */
    fun generateLocationRecommendations(context: RecommendationContext): List<Recommendation> {
        val location = context.location ?: return emptyList()

        return when (location.toLowerCase()) {
            "食堂", "cafeteria" -> generateCafeteriaRecommendations(context)
            "外卖", "delivery" -> generateDeliveryRecommendations(context)
            "餐厅", "restaurant" -> generateRestaurantRecommendations(context)
            "自制", "home", "home cooking" -> generateHomeCookingRecommendations(context)
            "快餐", "fast food" -> generateFastFoodRecommendations(context)
            else -> generateGeneralLocationRecommendations(location, context)
        }
    }

    /**
     * 生成食堂相关推荐
     */
    private fun generateCafeteriaRecommendations(context: RecommendationContext): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 1. 推荐食堂健康选择
        recommendations.add(
            Recommendation(
                id = generateRecommendationId(),
                type = RecommendationType.FOOD_SUGGESTION,
                title = "食堂健康搭配",
                description = "食堂用餐建议：\n" +
                        "• 一荤一素一主食\n" +
                        "• 选择清蒸、炖煮类菜品\n" +
                        "• 避免油炸和高盐菜品\n" +
                        "• 多选不同颜色的蔬菜",
                priority = Priority.MEDIUM,
                confidence = 0.8f,
                reason = "基于食堂用餐场景",
                actions = listOf(
                    Action.ShowFoodDetails(-206),
                    Action.AddToMealPlan(listOf(-50L, -51L, -52L)),
                    Action.DismissRecommendation("不需要")
                )
            )
        )

        // 2. 检查蛋白质摄入
        val proteinGap = context.nutritionalGaps?.find {
            it.nutrient == "蛋白质" || it.nutrient == "protein"
        }

        proteinGap?.let {
            if (it.gapPercentage > 20) {
                recommendations.add(
                    Recommendation(
                        id = generateRecommendationId(),
                        type = RecommendationType.NUTRITION_GAP,
                        title = "食堂补充蛋白质",
                        description = "蛋白质摄入不足，建议在食堂选择：\n" +
                                "• 鸡腿/鸡胸肉\n" +
                                "• 鱼肉\n" +
                                "• 豆腐/豆制品\n" +
                                "• 鸡蛋",
                        priority = Priority.HIGH,
                        confidence = 0.9f,
                        reason = "结合蛋白质缺口和食堂场景",
                        actions = listOf(
                            Action.ShowFoodDetails(-207),
                            Action.AddToMealPlan(listOf(-53L, -54L, -55L)),
                            Action.DismissRecommendation("已了解")
                        )
                    )
                )
            }
        }

        return recommendations
    }

    /**
     * 生成外卖相关推荐
     */
    private fun generateDeliveryRecommendations(context: RecommendationContext): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 基于用户偏好推荐
        val preferredCuisines = context.preferredCuisines ?: listOf("中餐", "西餐", "日料")
        val budgetRange = context.budgetRange ?: BudgetRange(20.0, 50.0)

        val title = when {
            preferredCuisines.contains("轻食") -> "健康外卖推荐"
            preferredCuisines.contains("中餐") -> "中式外卖选择"
            else -> "外卖用餐建议"
        }

        val description = buildString {
            append("根据您的偏好和预算(${budgetRange.min}-${budgetRange.max}元)，建议：\n")
            append("• 选择有营养标签的商家\n")
            append("• 备注少油少盐\n")
            append("• 搭配蔬菜或沙拉\n")
            append("• 控制主食分量")

            if (preferredCuisines.isNotEmpty()) {
                append("\n• 可尝试：${preferredCuisines.joinToString("、")}")
            }
        }

        recommendations.add(
            Recommendation(
                id = generateRecommendationId(),
                type = RecommendationType.MEAL_PLAN,
                title = title,
                description = description,
                priority = Priority.MEDIUM,
                confidence = 0.7f,
                reason = "基于外卖场景和您的偏好",
                actions = listOf(
                    Action.ShowFoodDetails(-208),
                    Action.AddToMealPlan(emptyList()), // 实际应该关联具体食物
                    Action.DismissRecommendation("不需要")
                ),
                metadata = mapOf(
                    "location" to "外卖",
                    "cuisines" to preferredCuisines,
                    "budget" to "${budgetRange.min}-${budgetRange.max}元"
                )
            )
        )

        return recommendations
    }

    /**
     * 生成餐厅相关推荐
     */
    private fun generateRestaurantRecommendations(context: RecommendationContext): List<Recommendation> {
        return listOf(
            Recommendation(
                id = generateRecommendationId(),
                type = RecommendationType.HABIT_IMPROVEMENT,
                title = "餐厅用餐技巧",
                description = "餐厅用餐健康技巧：\n" +
                        "• 先喝汤或吃蔬菜垫底\n" +
                        "• 选择烤、蒸、煮的烹饪方式\n" +
                        "• 控制主食和甜品的分量\n" +
                        "• 分享菜品，避免过量",
                priority = Priority.LOW,
                confidence = 0.6f,
                reason = "基于餐厅用餐场景",
                actions = listOf(
                    Action.DismissRecommendation("知道了")
                )
            )
        )
    }

    /**
     * 生成自制餐相关推荐
     */
    private fun generateHomeCookingRecommendations(context: RecommendationContext): List<Recommendation> {
        val cookingTime = context.cookingTimeAvailability ?: CookingTime.MODERATE

        val title = when (cookingTime) {
            CookingTime.QUICK -> "快速家常菜"
            CookingTime.MODERATE -> "营养家常菜"
            CookingTime.EXTENDED -> "精致家常菜"
        }

        val description = when (cookingTime) {
            CookingTime.QUICK -> "快速自制餐建议(<15分钟)：\n• 番茄炒蛋\n• 清炒时蔬\n• 即食肉类\n• 速食汤"
            CookingTime.MODERATE -> "营养自制餐建议(15-30分钟)：\n• 红烧鸡块\n• 清蒸鱼\n• 炒青菜\n• 米饭"
            CookingTime.EXTENDED -> "精致自制餐建议(>30分钟)：\n• 炖汤\n• 复杂菜肴\n• 多菜品搭配"
        }

        return listOf(
            Recommendation(
                id = generateRecommendationId(),
                type = RecommendationType.MEAL_PLAN,
                title = title,
                description = description,
                priority = Priority.MEDIUM,
                confidence = 0.75f,
                reason = "基于自制餐场景和可用时间",
                actions = listOf(
                    Action.ShowFoodDetails(-209),
                    Action.AddToMealPlan(listOf(-56L, -57L, -58L)),
                    Action.DismissRecommendation("不需要")
                ),
                metadata = mapOf(
                    "location" to "自制",
                    "cookingTime" to cookingTime.name
                )
            )
        )
    }

    /**
     * 生成快餐相关推荐
     */
    private fun generateFastFoodRecommendations(context: RecommendationContext): List<Recommendation> {
        return listOf(
            Recommendation(
                id = generateRecommendationId(),
                type = RecommendationType.HABIT_IMPROVEMENT,
                title = "快餐健康选择",
                description = "快餐店健康选择建议：\n" +
                        "• 选择烤的而不是炸的\n" +
                        "• 去掉酱料或选择低脂酱\n" +
                        "• 搭配蔬菜沙拉\n" +
                        "• 选择水或无糖饮料",
                priority = Priority.MEDIUM,
                confidence = 0.65f,
                reason = "基于快餐用餐场景",
                actions = listOf(
                    Action.ShowFoodDetails(-210),
                    Action.DismissRecommendation("了解")
                )
            )
        )
    }

    /**
     * 生成通用地点推荐
     */
    private fun generateGeneralLocationRecommendations(
        location: String,
        context: RecommendationContext
    ): List<Recommendation> {
        return listOf(
            Recommendation(
                id = generateRecommendationId(),
                type = RecommendationType.EDUCATIONAL,
                title = "在外用餐建议",
                description = "在任何地方用餐都要注意：\n" +
                        "• 均衡营养搭配\n" +
                        "• 控制食物分量\n" +
                        "• 选择健康的烹饪方式\n" +
                        "• 注意食物卫生",
                priority = Priority.LOW,
                confidence = 0.5f,
                reason = "基于当前用餐地点：$location",
                actions = listOf(
                    Action.DismissRecommendation("知道了")
                )
            )
        )
    }
}