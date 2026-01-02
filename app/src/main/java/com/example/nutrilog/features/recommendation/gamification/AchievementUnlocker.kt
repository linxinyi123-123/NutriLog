// app/src/main/java/com/nutrilog/features/recommendation/gamification/AchievementUnlocker.kt
package com.example.nutrilog.features.recommendation.gamification

import com.example.nutrilog.features.recommendation.interfaces.NutritionalGap
import com.example.nutrilog.features.recommendation.model.gamification.Achievement
import com.example.nutrilog.features.recommendation.model.gamification.Condition
import kotlinx.coroutines.flow.first

// 移除 @Inject 注解
class AchievementUnlocker(
    private val achievementRepository: AchievementRepository,
    // 这些依赖将在后续集成时添加，现在先注释掉
    // private val recordRepository: RecordRepository,
    // private val analysisService: NutritionAnalysisService
) {

    // 用于跟踪连续记录天数（简化实现）
    private val streakCache = mutableMapOf<Long, Int>()

    /**
     * 检查并解锁成就
     */
    suspend fun checkAchievements(userId: Long) {
        // 获取待解锁的成就
        val pendingAchievements = achievementRepository.getPendingAchievements(userId).first()

        pendingAchievements.forEach { achievement ->
            if (checkAchievementCondition(userId, achievement)) {
                // 解锁成就
                achievementRepository.unlockAchievement(userId, achievement.id)

                // 授予成就奖励（将在后续步骤实现）
                // grantAchievementRewards(userId, achievement)
            }
        }
    }

    /**
     * 检查单个成就条件
     */
    private suspend fun checkAchievementCondition(
        userId: Long,
        achievement: Achievement
    ): Boolean {
        return when (val condition = achievement.condition) {
            is Condition.StreakDays -> checkStreakCondition(userId, condition)
            is Condition.TotalRecords -> checkTotalRecordsCondition(userId, condition)
            is Condition.NutrientTarget -> checkNutrientTargetCondition(userId, condition)
            is Condition.FoodVariety -> checkFoodVarietyCondition(userId, condition)
            is Condition.Composite -> checkCompositeCondition(userId, condition)
            else -> false
        }
    }

    /**
     * 检查连续记录条件（简化实现）
     */
    private suspend fun checkStreakCondition(
        userId: Long,
        condition: Condition.StreakDays
    ): Boolean {
        // 简化实现：使用内存缓存
        val currentStreak = streakCache.getOrDefault(userId, 0)
        return currentStreak >= condition.days
    }

    /**
     * 检查总记录数条件（简化实现）
     */
    private suspend fun checkTotalRecordsCondition(
        userId: Long,
        condition: Condition.TotalRecords
    ): Boolean {
        // 简化实现：假设已经有足够的记录
        // 实际实现需要从recordRepository获取记录数
        val mockRecordCount = 15 // 模拟15条记录
        return mockRecordCount >= condition.count
    }

    /**
     * 检查营养目标条件（简化实现）
     */
    private suspend fun checkNutrientTargetCondition(
        userId: Long,
        condition: Condition.NutrientTarget
    ): Boolean {
        // 简化实现：使用模拟数据
        // 实际实现需要从analysisService获取营养分析
        val mockNutrientData = mapOf(
            "protein" to 65.0,
            "fiber" to 22.0,
            "calcium" to 750.0
        )

        val currentValue = mockNutrientData[condition.nutrient] ?: 0.0
        return currentValue >= condition.target
    }

    /**
     * 检查食物多样性条件（简化实现）
     */
    private suspend fun checkFoodVarietyCondition(
        userId: Long,
        condition: Condition.FoodVariety
    ): Boolean {
        // 简化实现：假设已经有足够的食物种类
        // 实际实现需要从recordRepository获取食物种类数
        val mockFoodCategories = 12 // 模拟12种食物类别
        return mockFoodCategories >= condition.categories
    }

    /**
     * 检查复合条件
     */
    private suspend fun checkCompositeCondition(
        userId: Long,
        condition: Condition.Composite
    ): Boolean {
        return condition.conditions.all { subCondition ->
            when (subCondition) {
                is Condition.StreakDays -> checkStreakCondition(userId, subCondition)
                is Condition.TotalRecords -> checkTotalRecordsCondition(userId, subCondition)
                is Condition.NutrientTarget -> checkNutrientTargetCondition(userId, subCondition)
                is Condition.FoodVariety -> checkFoodVarietyCondition(userId, subCondition)
                is Condition.Composite -> checkCompositeCondition(userId, subCondition)
                else -> false
            }
        }
    }

    /**
     * 更新连续记录天数（在用户记录饮食时调用）
     */
    fun updateStreak(userId: Long, hasRecordedToday: Boolean) {
        val currentStreak = streakCache.getOrDefault(userId, 0)

        if (hasRecordedToday) {
            // 今天已经记录，连续天数+1
            streakCache[userId] = currentStreak + 1
        } else {
            // 今天没有记录，重置连续天数
            streakCache[userId] = 0
        }
    }
}