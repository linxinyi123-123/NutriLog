package com.nutrilog.features.recommendation.database

import com.nutrilog.features.recommendation.database.entity.AchievementEntity
import com.nutrilog.features.recommendation.database.entity.RecommendationRuleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecommendationDatabaseInitializer(
    private val database: RecommendationDatabase
) {

    fun initializeForUser(userId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            // 为用户复制模板成就
            copyTemplateAchievementsForUser(userId)

            // 插入初始推荐规则
            insertDefaultRecommendationRules()
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
        // 这里可以插入一些默认的推荐规则
        // 例如：当蛋白质不足时推荐高蛋白食物
        // 这些规则将在D4的规则引擎中使用

        // 由于任务清单中没有RequirementRuleEntity的定义，这里先注释
        // 等D4时再实现
        /*
        val defaultRules = listOf(
            RecommendationRuleEntity(
                id = 1,
                type = "NUTRITION_GAP",
                condition = "protein_gap > 0.3",
                action = "suggest_protein_foods",
                priority = "HIGH",
                message = "检测到蛋白质摄入不足，建议增加蛋白质食物"
            )
        )
        */
    }
}