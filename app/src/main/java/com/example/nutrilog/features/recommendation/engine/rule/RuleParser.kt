// app/src/main/java/com/nutrilog/features/recommendation/engine/rule/RuleParser.kt
package com.example.nutrilog.features.recommendation.engine.rule

import com.example.nutrilog.features.recommendation.database.entity.RecommendationRuleEntity
import com.example.nutrilog.features.recommendation.model.Priority
import org.json.JSONObject
import timber.log.Timber

class RuleParser {

    /**
     * 将数据库实体转换为业务规则
     * 注意：这里condition和action字段是JSON字符串
     */
    fun parseRule(entity: RecommendationRuleEntity): RecommendationRule? {
        return try {
            // 解析条件
            val condition = parseCondition(entity.condition)
            // 解析动作
            val action = parseAction(entity.action)

            if (condition == null || action == null) {
                Timber.w("规则解析失败: condition或action为null, ruleId=${entity.id}")
                return null
            }

            RecommendationRule(
                id = entity.id,
                name = "规则${entity.id}",
                type = RuleType.valueOf(entity.type),
                condition = condition,
                action = action,
                priority = Priority.valueOf(entity.priority),
                message = entity.message
            )
        } catch (e: Exception) {
            Timber.e(e, "规则解析异常, ruleId=${entity.id}")
            null
        }
    }

    /**
     * 解析条件字符串
     * 格式示例: {"type":"NUTRIENT_GAP","nutrient":"protein","threshold":30,"comparison":"GREATER_THAN"}
     */
    private fun parseCondition(conditionString: String): RuleCondition? {
        return try {
            val json = JSONObject(conditionString)
            val type = json.getString("type")

            when (type) {
                "NUTRIENT_GAP" -> {
                    RuleCondition.NutrientGap(
                        nutrient = json.getString("nutrient"),
                        threshold = json.getDouble("threshold"),
                        comparison = Comparison.valueOf(json.getString("comparison"))
                    )
                }
                "HEALTH_SCORE" -> {
                    RuleCondition.HealthScore(
                        score = json.getInt("score"),
                        comparison = Comparison.valueOf(json.getString("comparison"))
                    )
                }
                "GOAL_PROGRESS" -> {
                    RuleCondition.GoalProgress(
                        goalType = json.getString("goalType"),
                        progress = json.getDouble("progress").toFloat(),
                        comparison = Comparison.valueOf(json.getString("comparison"))
                    )
                }
                else -> {
                    Timber.w("未知的条件类型: $type")
                    null
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "条件解析失败: $conditionString")
            null
        }
    }

    /**
     * 解析动作字符串
     * 格式示例: {"type":"SUGGEST_FOODS","foodCategories":["高蛋白"],"reason":"蛋白质补充"}
     */
    private fun parseAction(actionString: String): RuleAction? {
        return try {
            val json = JSONObject(actionString)
            val type = json.getString("type")

            when (type) {
                "SUGGEST_FOODS" -> {
                    RuleAction.SuggestFoods(
                        foodCategories = parseStringArray(json, "foodCategories"),
                        reason = json.getString("reason"),
                        mealType = json.optString("mealType", null)
                    )
                }
                "SHOW_EDUCATIONAL_TIP" -> {
                    RuleAction.ShowEducationalTip(
                        tipId = json.getLong("tipId"),
                        category = json.getString("category")
                    )
                }
                "CREATE_MEAL_PLAN" -> {
                    RuleAction.CreateMealPlan(
                        planType = json.getString("planType"),
                        duration = json.getInt("duration"),
                        targetCalories = json.optInt("targetCalories").takeIf { it > 0 }
                    )
                }
                else -> {
                    Timber.w("未知的动作类型: $type")
                    null
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "动作解析失败: $actionString")
            null
        }
    }

    private fun parseStringArray(json: JSONObject, key: String): List<String> {
        return try {
            val array = json.getJSONArray(key)
            (0 until array.length()).map { array.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * 创建默认规则（用于初始化数据库）
     */
    fun createDefaultRules(): List<RecommendationRule> {
        return listOf(
            // 1. 蛋白质严重不足规则
            RecommendationRule(
                id = 1,
                name = "蛋白质补充建议",
                type = RuleType.NUTRITION_GAP,
                condition = RuleCondition.NutrientGap(
                    nutrient = "protein",
                    threshold = 30.0,
                    comparison = Comparison.GREATER_THAN
                ),
                action = RuleAction.SuggestFoods(
                    foodCategories = listOf("高蛋白"),
                    reason = "检测到蛋白质摄入严重不足",
                    mealType = "午餐"
                ),
                priority = Priority.HIGH,
                message = "蛋白质摄入不足超过30%，建议增加高蛋白食物",
                cooldown = 24 * 60 * 60 * 1000 // 24小时冷却
            ),

            // 2. 健康评分过低规则
            RecommendationRule(
                id = 2,
                name = "健康评分改善",
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
                message = "您的健康评分较低，建议查看营养知识"
            ),

            // 3. 膳食纤维不足规则
            RecommendationRule(
                id = 3,
                name = "膳食纤维补充",
                type = RuleType.NUTRITION_GAP,
                condition = RuleCondition.NutrientGap(
                    nutrient = "fiber",
                    threshold = 20.0,
                    comparison = Comparison.GREATER_THAN
                ),
                action = RuleAction.SuggestFoods(
                    foodCategories = listOf("高纤维"),
                    reason = "膳食纤维摄入不足",
                    mealType = null
                ),
                priority = Priority.MEDIUM,
                message = "膳食纤维摄入不足，建议增加蔬菜水果"
            ),

            // 4. 减重目标进度缓慢规则
            RecommendationRule(
                id = 4,
                name = "减重计划调整",
                type = RuleType.GOAL_BASED,
                condition = RuleCondition.GoalProgress(
                    goalType = "WEIGHT_LOSS",
                    progress = 0.3f,
                    comparison = Comparison.LESS_THAN
                ),
                action = RuleAction.CreateMealPlan(
                    planType = "低热量饮食",
                    duration = 7,
                    targetCalories = 1500
                ),
                priority = Priority.MEDIUM,
                message = "减重进度较慢，建议尝试一周低热量饮食计划"
            ),

            // 5. 复合规则：蛋白质不足且为晚餐时间
            RecommendationRule(
                id = 5,
                name = "晚餐蛋白质补充",
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
                            score = 18, // 18点以后
                            comparison = Comparison.GREATER_THAN_OR_EQUAL
                        )
                    )
                ),
                action = RuleAction.SuggestFoods(
                    foodCategories = listOf("高蛋白", "低脂肪"),
                    reason = "晚餐时间且蛋白质摄入不足",
                    mealType = "晚餐"
                ),
                priority = Priority.MEDIUM,
                message = "晚餐时间建议补充优质蛋白，选择低脂肪蛋白来源"
            )
        )
    }
}