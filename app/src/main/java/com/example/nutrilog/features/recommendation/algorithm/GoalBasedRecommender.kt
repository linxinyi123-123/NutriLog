package com.example.nutrilog.features.recommendation.algorithm

import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.interfaces.NutritionalGap
import com.example.nutrilog.features.recommendation.model.*
import kotlin.math.abs

/**
 * 基于健康目标的推荐器
 */
class GoalBasedRecommender : BaseRecommender() {

    /**
     * 生成基于健康目标的推荐
     */
    fun generateGoalRecommendations(
        goals: List<HealthGoal>,
        context: RecommendationContext
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 只处理活跃状态的目标
        val activeGoals = goals.filter { it.status == GoalStatus.ACTIVE }

        activeGoals.forEach { goal ->
            val goalRecommendations = when (goal.type) {
                GoalType.WEIGHT_LOSS -> generateWeightLossRecommendations(goal, context)
                GoalType.WEIGHT_GAIN -> generateWeightGainRecommendations(goal, context)
                GoalType.MUSCLE_GAIN -> generateMuscleGainRecommendations(goal, context)
                GoalType.BODY_FAT_REDUCTION -> generateBodyFatReductionRecommendations(goal, context)
                GoalType.HEALTH_IMPROVEMENT -> generateHealthImprovementRecommendations(goal, context)
                GoalType.NUTRIENT_BALANCE -> generateNutrientBalanceRecommendations(goal, context)
            }
            recommendations.addAll(goalRecommendations)
        }

        return deduplicateRecommendations(sortRecommendations(recommendations))
    }

    /**
     * 生成减重相关推荐
     */
    private fun generateWeightLossRecommendations(
        goal: HealthGoal,
        context: RecommendationContext
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 检查热量控制
        if (goal.progress < 0.3f) { // 进度小于30%
            recommendations.add(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.MEAL_PLAN,
                    title = "减重起步建议",
                    description = "减重初期建议：\n" +
                            "• 控制每日热量摄入在${goal.target.value.toInt()}${goal.target.unit}以内\n" +
                            "• 增加蔬菜摄入，减少高热量食物\n" +
                            "• 保持规律三餐，避免夜宵",
                    priority = Priority.MEDIUM,
                    confidence = 0.8f,
                    reason = "基于减重目标的初期指导",
                    actions = listOf(
                        Action.ShowFoodDetails(-101),
                        Action.AddToMealPlan(listOf(-20L, -21L, -22L)),
                        Action.DismissRecommendation("稍后提醒")
                    ),
                    metadata = mapOf(
                        "goalType" to goal.type.name,
                        "targetValue" to goal.target.value,
                        "progress" to goal.progress
                    )
                )
            )
        }

        // 检查蛋白质摄入（减重期间需要充足蛋白质保护肌肉）
        val proteinGap = findProteinGap(context.nutritionalGaps)
        proteinGap?.let {
            if (it.gapPercentage > 20) {
                recommendations.add(
                    Recommendation(
                        id = generateRecommendationId(),
                        type = RecommendationType.NUTRITION_GAP,
                        title = "减重期蛋白质保护",
                        description = "减重期间需要充足蛋白质保护肌肉，当前蛋白质摄入不足${it.gapPercentage.toInt()}%。\n" +
                                "建议保证每日蛋白质摄入。",
                        priority = Priority.HIGH,
                        confidence = 0.9f,
                        reason = "减重目标结合营养分析",
                        actions = listOf(
                            Action.ShowFoodDetails(-1),
                            Action.AddToMealPlan(listOf(-1L, -2L, -3L)),
                            Action.DismissRecommendation("已了解")
                        )
                    )
                )
            }
        }

        return recommendations
    }

    /**
     * 生成增肌相关推荐
     */
    private fun generateMuscleGainRecommendations(
        goal: HealthGoal,
        context: RecommendationContext
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 检查蛋白质摄入
        val proteinGap = findProteinGap(context.nutritionalGaps)

        if (proteinGap != null && proteinGap.gapPercentage > 15) {
            recommendations.add(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.NUTRITION_GAP,
                    title = "增肌期蛋白质需求",
                    description = "增肌期间需要充足蛋白质，当前蛋白质摄入不足${proteinGap.gapPercentage.toInt()}%。\n" +
                            "建议每日蛋白质摄入量：每公斤体重1.5-2.0克。",
                    priority = Priority.HIGH,
                    confidence = 0.9f,
                    reason = "增肌目标结合营养分析",
                    actions = listOf(
                        Action.ShowFoodDetails(-1),
                        Action.AddToMealPlan(listOf(-1L, -2L, -3L)),
                        Action.DismissRecommendation("已了解")
                    )
                )
            )
        }

        // 建议餐次安排
        if (goal.progress < 0.5f) {
            recommendations.add(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.MEAL_PLAN,
                    title = "增肌饮食安排",
                    description = "增肌建议饮食计划：\n" +
                            "• 每日5-6餐，少量多餐\n" +
                            "• 训练前后补充蛋白质和碳水\n" +
                            "• 保证充足热量盈余",
                    priority = Priority.MEDIUM,
                    confidence = 0.7f,
                    reason = "基于增肌目标的饮食建议",
                    actions = listOf(
                        Action.ShowFoodDetails(-102),
                        Action.AddToMealPlan(listOf(-23L, -24L, -25L)),
                        Action.DismissRecommendation("稍后提醒")
                    )
                )
            )
        }

        return recommendations
    }

    /**
     * 生成营养均衡相关推荐
     */
    private fun generateNutrientBalanceRecommendations(
        goal: HealthGoal,
        context: RecommendationContext
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // 检查是否有多个营养缺口
        val significantGaps = context.nutritionalGaps.filter {
            it.severity == com.example.nutrilog.features.recommendation.interfaces.Severity.SEVERE ||
                    it.severity == com.example.nutrilog.features.recommendation.interfaces.Severity.MODERATE
        }

        if (significantGaps.size >= 2) {
            recommendations.add(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.HABIT_IMPROVEMENT,
                    title = "多样化饮食改善",
                    description = "检测到${significantGaps.size}个营养素摄入不足。\n" +
                            "建议增加食物种类，实现营养均衡：\n" +
                            "• 每天摄入5种以上蔬菜\n" +
                            "• 搭配不同蛋白质来源\n" +
                            "• 选择全谷物主食",
                    priority = Priority.MEDIUM,
                    confidence = 0.75f,
                    reason = "基于多个营养缺口分析",
                    actions = listOf(
                        Action.ShowFoodDetails(-103),
                        Action.AddToMealPlan(listOf(-26L, -27L, -28L)),
                        Action.DismissRecommendation("已了解")
                    )
                )
            )
        }

        // 检查食物多样性
        if (context.mealPatterns?.foodVariety != null && context.mealPatterns.foodVariety < 15) {
            recommendations.add(
                Recommendation(
                    id = generateRecommendationId(),
                    type = RecommendationType.HABIT_IMPROVEMENT,
                    title = "增加食物多样性",
                    description = "当前食物种类较少（${context.mealPatterns.foodVariety}种）。\n" +
                            "建议每周尝试2-3种新食物，增加饮食多样性。",
                    priority = Priority.LOW,
                    confidence = 0.6f,
                    reason = "基于食物多样性分析",
                    actions = listOf(
                        Action.ShowFoodDetails(-104),
                        Action.DismissRecommendation("知道了")
                    )
                )
            )
        }

        return recommendations
    }

    /**
     * 生成增重相关推荐（简化版）
     */
    private fun generateWeightGainRecommendations(
        goal: HealthGoal,
        context: RecommendationContext
    ): List<Recommendation> {
        return listOf(
            Recommendation(
                id = generateRecommendationId(),
                type = RecommendationType.MEAL_PLAN,
                title = "健康增重建议",
                description = "健康增重要点：\n" +
                        "• 保证热量盈余，增加健康食物摄入\n" +
                        "• 增加优质蛋白质和健康脂肪\n" +
                        "• 配合力量训练效果更佳",
                priority = Priority.MEDIUM,
                confidence = 0.7f,
                reason = "基于增重目标的指导",
                actions = listOf(
                    Action.ShowFoodDetails(-105),
                    Action.AddToMealPlan(listOf(-29L, -30L, -31L)),
                    Action.DismissRecommendation("稍后提醒")
                )
            )
        )
    }

    /**
     * 生成减脂相关推荐（简化版）
     */
    private fun generateBodyFatReductionRecommendations(
        goal: HealthGoal,
        context: RecommendationContext
    ): List<Recommendation> {
        return listOf(
            Recommendation(
                id = generateRecommendationId(),
                type = RecommendationType.MEAL_PLAN,
                title = "减脂饮食建议",
                description = "减脂饮食要点：\n" +
                        "• 控制总热量，保证蛋白质摄入\n" +
                        "• 选择低GI碳水，增加膳食纤维\n" +
                        "• 合理安排碳水摄入时间",
                priority = Priority.MEDIUM,
                confidence = 0.7f,
                reason = "基于减脂目标的指导",
                actions = listOf(
                    Action.ShowFoodDetails(-106),
                    Action.AddToMealPlan(listOf(-32L, -33L, -34L)),
                    Action.DismissRecommendation("稍后提醒")
                )
            )
        )
    }

    /**
     * 生成健康改善相关推荐（简化版）
     */
    private fun generateHealthImprovementRecommendations(
        goal: HealthGoal,
        context: RecommendationContext
    ): List<Recommendation> {
        return listOf(
            Recommendation(
                id = generateRecommendationId(),
                type = RecommendationType.HABIT_IMPROVEMENT,
                title = "整体健康改善",
                description = "健康改善建议：\n" +
                        "• 保持规律作息和充足睡眠\n" +
                        "• 均衡饮食，多吃天然食物\n" +
                        "• 适度运动，保持积极心态",
                priority = Priority.MEDIUM,
                confidence = 0.7f,
                reason = "基于健康改善目标的指导",
                actions = listOf(
                    Action.DismissRecommendation("知道了")
                )
            )
        )
    }

    /**
     * 查找蛋白质缺口
     */
    private fun findProteinGap(gaps: List<NutritionalGap>): NutritionalGap? {
        return gaps.find {
            it.nutrient.equals("protein", ignoreCase = true) ||
                    it.nutrient.contains("蛋白质")
        }
    }
}