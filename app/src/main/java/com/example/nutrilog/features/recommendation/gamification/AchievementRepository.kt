// app/src/main/java/com/nutrilog/features/recommendation/gamification/AchievementRepository.kt
package com.example.nutrilog.features.recommendation.gamification

import com.example.nutrilog.features.recommendation.database.dao.AchievementDao
import com.example.nutrilog.features.recommendation.database.entity.AchievementEntity
import com.example.nutrilog.features.recommendation.model.gamification.Achievement
import com.example.nutrilog.features.recommendation.model.gamification.AchievementType
import com.example.nutrilog.features.recommendation.model.gamification.Condition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

// 移除 @Inject 注解，改为普通构造函数
class AchievementRepository(
    private val achievementDao: AchievementDao
) {

    /**
     * 获取用户的所有成就（转换为领域模型）
     */
    fun getUserAchievements(userId: Long): Flow<List<Achievement>> {
        return achievementDao.getUserAchievements(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * 获取用户已解锁的成就
     */
    fun getUnlockedAchievements(userId: Long): Flow<List<Achievement>> {
        return achievementDao.getUnlockedAchievements(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * 获取用户待解锁的成就
     */
    fun getPendingAchievements(userId: Long): Flow<List<Achievement>> {
        return achievementDao.getLockedAchievements(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * 解锁成就
     */
    suspend fun unlockAchievement(userId: Long, achievementId: Long) {
        achievementDao.unlockAchievement(achievementId, System.currentTimeMillis())
    }

    /**
     * 更新成就进度
     */
    suspend fun updateAchievementProgress(achievementId: Long, progress: Float) {
        achievementDao.updateProgress(achievementId, progress)
    }

    /**
     * 获取用户成就统计
     */
    suspend fun getUserAchievementStats(userId: Long): UserStats {
        val unlockedCount = achievementDao.getUnlockedCount(userId)
        val totalAchievements = achievementDao.getUserAchievements(userId).first().size
        val totalPoints = calculateTotalPoints(userId)

        return UserStats(
            userId = userId,
            totalPoints = totalPoints,
            level = LevelCalculator.calculateLevel(totalPoints),
            unlockedAchievements = unlockedCount,
            totalAchievements = totalAchievements,
            achievementCompletionRate = if (totalAchievements > 0) {
                unlockedCount.toFloat() / totalAchievements
            } else 0f
        )
    }

    /**
     * 计算用户总积分
     */
    private suspend fun calculateTotalPoints(userId: Long): Int {
        val unlockedAchievements = achievementDao.getUnlockedAchievements(userId).first()
        return unlockedAchievements.sumOf { it.points }
    }

    /**
     * 将数据库实体转换为领域模型
     */
    private fun AchievementEntity.toDomainModel(): Achievement {
        // 注意：这里我们简化了Condition的解析
        // 实际项目中，你可能需要从数据库存储的字符串中解析出Condition
        // 这里我们先使用一个简单的Condition.StreakDays(1)作为占位符
        val condition = when (this.id) {
            1L -> Condition.StreakDays(1)
            2L -> Condition.StreakDays(7)
            3L -> Condition.TotalRecords(10)
            4L -> Condition.NutrientTarget("protein", 60.0)
            5L -> Condition.FoodVariety(10)
            else -> Condition.StreakDays(1)
        }

        return Achievement(
            id = this.id,
            name = this.name,
            description = this.description,
            type = AchievementType.valueOf(this.type),
            icon = this.icon,
            points = this.points,
            condition = condition,
            unlockedAt = this.unlockedAt
        )
    }
}