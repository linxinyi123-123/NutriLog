package com.example.nutrilog.features.recommendation.algorithm

import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.model.*
import java.time.LocalTime

/**
 * 基于时间的推荐器
 */
class TimeBasedRecommender : BaseRecommender() {

    /**
     * 生成基于时间的推荐
     */
    fun generateTimeRecommendations(context: RecommendationContext): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 获取当前时间
        val currentTime = LocalTime.of(context.currentHour, 0) // 简化处理，假设分钟为0

        // 1. 用餐时间提醒
        val mealReminderRecs = generateMealReminders(currentTime, context)
        recommendations.addAll(mealReminderRecs)

        // 2. 基于时间的营养建议
        val timeBasedNutritionRecs = generateTimeBasedNutritionAdvice(currentTime, context)
        recommendations.addAll(timeBasedNutritionRecs)

        // 3. 夜宵/加餐建议
        if (MealTime.isLateNightSnackTime(currentTime)) {
            val lateNightRecs = generateLateNightSnackAdvice(context)
            recommendations.addAll(lateNightRecs)
        }

        // 4. 忙碌时间快速餐建议
        if (MealTime.isBusyTime(currentTime)) {
            val quickMealRecs = generateQuickMealAdvice(context)
            recommendations.addAll(quickMealRecs)
        }

        return sortRecommendations(recommendations)
    }

    /**
     * 生成用餐时间提醒
     */
    private fun generateMealReminders(
        currentTime: LocalTime,
        context: RecommendationContext
    ): List<Recommendation> {
        val mealType = MealTime.getMealTypeByTime(currentTime)
        val recommendations = mutableListOf<Recommendation>()

        // 检查是否已经记录了这个餐次的饮食
        val hasRecordedThisMeal = hasRecordedMeal(mealType, context)

        if (!hasRecordedThisMeal) {
            // 生成用餐提醒
            val title = when (mealType) {
                "早餐" -> "早餐时间到！"
                "午餐" -> "午餐时间提醒"
                "晚餐" -> "晚餐时间提醒"
                else -> "加餐时间提醒"
            }

            val description = when (mealType) {
                "早餐" -> "记得吃早餐，为新的一天补充能量！建议搭配蛋白质和全谷物。"
                "午餐" -> "午餐时间到了，注意营养均衡。建议一荤一素一主食。"
                "晚餐" -> "晚餐宜清淡，避免过于油腻。建议蔬菜为主，蛋白质适量。"
                else -> "加餐时间，可以选择水果、坚果或酸奶等健康零食。"
            }

            val priority = when (mealType) {
                "早餐" -> Priority.HIGH  // 早餐很重要
                "午餐" -> Priority.MEDIUM
                "晚餐" -> Priority.MEDIUM
                else -> Priority.LOW
            }

            recommendations.add(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.MEAL_PLAN,
                    title = title,
                    description = description,
                    priority = priority,
                    confidence = 0.8f,
                    reason = "基于当前时间(${currentTime.hour}:${currentTime.minute})的用餐提醒",
                    actions = listOf(
                        Action.ShowFoodDetails(-200 + mealType.hashCode().toLong()),
                        Action.AddToMealPlan(emptyList()),
                        Action.DismissRecommendation("稍后提醒")
                    ),
                    metadata = mapOf(
                        "mealType" to mealType,
                        "currentTime" to currentTime.toString(),
                        "isReminder" to true
                    )
                )
            )
        }

        return recommendations
    }

    /**
     * 生成基于时间的营养建议
     */
    private fun generateTimeBasedNutritionAdvice(
        currentTime: LocalTime,
        context: RecommendationContext
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        when {
            currentTime.isBefore(LocalTime.of(10, 0)) -> {
                // 早上：建议高蛋白早餐
                recommendations.add(
                    Recommendation(
                        id = generateRecommendationId(),
                        type = RecommendationType.FOOD_SUGGESTION,
                        title = "早晨能量补充",
                        description = "早餐建议包含优质蛋白质，如鸡蛋、牛奶、豆浆，有助于提高注意力和代谢。",
                        priority = Priority.MEDIUM,
                        confidence = 0.7f,
                        reason = "早晨是补充蛋白质的好时机",
                        actions = listOf(
                            Action.ShowFoodDetails(-201),
                            Action.DismissRecommendation("知道了")
                        )
                    )
                )
            }

            currentTime.isBetween(14, 0, 16, 0) -> {
                // 下午：建议健康加餐
                recommendations.add(
                    Recommendation(
                        id = generateRecommendationId(),
                        type = RecommendationType.FOOD_SUGGESTION,
                        title = "下午能量补充",
                        description = "下午3-4点是血糖低谷，建议补充水果、坚果或酸奶，避免高糖零食。",
                        priority = Priority.LOW,
                        confidence = 0.6f,
                        reason = "下午时段容易疲劳，需要健康加餐",
                        actions = listOf(
                            Action.ShowFoodDetails(-202),
                            Action.DismissRecommendation("不需要")
                        )
                    )
                )
            }

            currentTime.isAfter(LocalTime.of(19, 0)) -> {
                // 晚上：建议清淡饮食
                recommendations.add(
                    Recommendation(
                        id = generateRecommendationId(),
                        type = RecommendationType.HABIT_IMPROVEMENT,
                        title = "晚餐后饮食建议",
                        description = "晚餐后尽量避免高热量食物，如果饿了可以选择少量水果或无糖酸奶。",
                        priority = Priority.LOW,
                        confidence = 0.65f,
                        reason = "晚上代谢较慢，应控制热量摄入",
                        actions = listOf(
                            Action.DismissRecommendation("了解")
                        )
                    )
                )
            }
        }

        return recommendations
    }

    /**
     * 生成夜宵建议
     */
    private fun generateLateNightSnackAdvice(context: RecommendationContext): List<Recommendation> {
        // 检查今日热量摄入
        val todayCalories = estimateTodayCalories(context)
        val targetCalories = context.healthGoals
            ?.find { it.type == GoalType.WEIGHT_LOSS }?.target?.value ?: 2000.0

        return if (todayCalories > targetCalories * 0.9) {
            // 热量已接近目标，推荐低热量夜宵
            listOf(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.FOOD_SUGGESTION,
                    title = "健康夜宵选择",
                    description = "今日热量摄入已接近目标。推荐低热量夜宵：\n" +
                            "• 黄瓜、小番茄\n" +
                            "• 无糖酸奶\n" +
                            "• 少量水果\n" +
                            "• 低脂牛奶",
                    priority = Priority.LOW,
                    confidence = 0.7f,
                    reason = "基于今日热量摄入和目标值",
                    actions = listOf(
                        Action.ShowFoodDetails(-203),
                        Action.AddToMealPlan(listOf(-40L, -41L, -42L)),
                        Action.DismissRecommendation("不需要")
                    )
                )
            )
        } else {
            // 热量充足，推荐健康夜宵
            listOf(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.FOOD_SUGGESTION,
                    title = "夜宵建议",
                    description = "如果感到饥饿，可以选择健康夜宵：\n" +
                            "• 香蕉+牛奶\n" +
                            "• 全麦面包\n" +
                            "• 低脂酸奶\n" +
                            "• 少量坚果",
                    priority = Priority.LOW,
                    confidence = 0.6f,
                    reason = "基于健康饮食习惯",
                    actions = listOf(
                        Action.ShowFoodDetails(-204),
                        Action.DismissRecommendation("不饿")
                    )
                )
            )
        }
    }

    /**
     * 生成快速简餐建议
     */
    private fun generateQuickMealAdvice(context: RecommendationContext): List<Recommendation> {
        return listOf(
            Recommendation(
                id = generateRecommendationId(),
                type = RecommendationType.MEAL_PLAN,
                title = "忙碌时的快速餐",
                description = "忙碌时建议快速营养餐：\n" +
                        "• 三明治+牛奶\n" +
                        "• 饭团+蔬菜汁\n" +
                        "• 燕麦片+坚果\n" +
                        "• 即食鸡胸肉+全麦面包",
                priority = Priority.MEDIUM,
                confidence = 0.75f,
                reason = "基于当前忙碌时段",
                actions = listOf(
                    Action.ShowFoodDetails(-205),
                    Action.AddToMealPlan(listOf(-43L, -44L, -45L)),
                    Action.DismissRecommendation("稍后查看")
                )
            )
        )
    }

    /**
     * 检查是否已经记录了这个餐次的饮食
     */
    private fun hasRecordedMeal(mealType: String, context: RecommendationContext): Boolean {
        // 简化处理：假设根据上下文中的recentMeals判断
        // 实际应该从数据库查询
        return when (mealType) {
            "早餐" -> context.currentHour < 10 // 如果当前时间小于10点，可能还没吃早餐
            "午餐" -> context.currentHour < 14
            "晚餐" -> context.currentHour < 21
            else -> false
        }
    }

    /**
     * 估算今日热量摄入（简化版）
     */
    private fun estimateTodayCalories(context: RecommendationContext): Double {
        // 简化处理，返回一个估计值
        // 实际应该从数据库查询今日记录
        return 1800.0
    }

    /**
     * 扩展函数：判断时间是否在范围内
     */
    private fun LocalTime.isBetween(startHour: Int, startMinute: Int,
                                    endHour: Int, endMinute: Int): Boolean {
        val start = LocalTime.of(startHour, startMinute)
        val end = LocalTime.of(endHour, endMinute)
        return (this == start || this.isAfter(start)) &&
                (this == end || this.isBefore(end))
    }
}