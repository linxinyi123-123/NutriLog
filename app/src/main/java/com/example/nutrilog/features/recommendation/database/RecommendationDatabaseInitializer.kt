package com.example.nutrilog.features.recommendation.database

import com.example.nutrilog.features.recommendation.database.entity.AchievementEntity
import com.example.nutrilog.features.recommendation.database.entity.RecommendationRuleEntity
import com.example.nutrilog.features.recommendation.engine.rule.RuleAction
import com.example.nutrilog.features.recommendation.engine.rule.RuleCondition
import com.example.nutrilog.features.recommendation.engine.rule.RuleParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecommendationDatabaseInitializer(
    private val database: RecommendationDatabase
) {

    suspend fun initializeForUser(userId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            // 为用户复制模板成就
            copyTemplateAchievementsForUser(userId)

            // 插入初始推荐规则（如果还没有规则）
            if (database.recommendationRuleDao().getRuleCount() == 0) {
                insertDefaultRecommendationRules()
            }
        }
    }

    private suspend fun copyTemplateAchievementsForUser(userId: Long) {
        val templateAchievements = database.achievementDao().getUserAchievements(0L)
        templateAchievements.collect { achievements ->
            val userAchievements = achievements.map { template ->
                AchievementEntity(
                    id = template.id,
                    userId = userId,
                    name = template.name,
                    description = template.description,
                    type = template.type,
                    icon = template.icon,
                    points = template.points,
                    isUnlocked = false,
                    unlockedAt = null,
                    progress = 0f
                )
            }

            database.achievementDao().insertAll(userAchievements)
        }
    }

    private suspend fun insertDefaultRecommendationRules() {
        val ruleParser = RuleParser()
        val defaultRules = ruleParser.createDefaultRules()

        val ruleEntities = defaultRules.map { rule ->
            RecommendationRuleEntity(
                id = rule.id,
                type = rule.type.name,
                condition = serializeCondition(rule.condition), // 需要实现序列化方法
                action = serializeAction(rule.action),         // 需要实现序列化方法
                priority = rule.priority.name,
                message = rule.message
            )
        }

        database.recommendationRuleDao().insertAll(ruleEntities)
    }

    // 添加序列化方法
    private fun serializeCondition(condition: RuleCondition): String {
        return when (condition) {
            is RuleCondition.NutrientGap -> {
                """{"type":"NUTRIENT_GAP","nutrient":"${condition.nutrient}","threshold":${condition.threshold},"comparison":"${condition.comparison.name}"}"""
            }
            is RuleCondition.HealthScore -> {
                """{"type":"HEALTH_SCORE","score":${condition.score},"comparison":"${condition.comparison.name}"}"""
            }
            is RuleCondition.GoalProgress -> {
                """{"type":"GOAL_PROGRESS","goalType":"${condition.goalType}","progress":${condition.progress},"comparison":"${condition.comparison.name}"}"""
            }
            else -> "{}"
        }
    }

    private fun serializeAction(action: RuleAction): String {
        return when (action) {
            is RuleAction.SuggestFoods -> {
                val categoriesJson = action.foodCategories.joinToString(",") { "\"$it\"" }
                """{"type":"SUGGEST_FOODS","foodCategories":[$categoriesJson],"reason":"${action.reason}"${if (action.mealType != null) ",\"mealType\":\"${action.mealType}\"" else ""}}"""
            }

            is RuleAction.ShowEducationalTip -> {
                """{"type":"SHOW_EDUCATIONAL_TIP","tipId":${action.tipId},"category":"${action.category}"}"""
            }

            else -> "{}"
        }
    }
}