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
        // 为每个成就ID分配对应的解锁条件
        val condition = when (this.id) {
            // 基础记录成就
            1L -> Condition.StreakDays(1)          // 首次记录
            2L -> Condition.StreakDays(3)          // 连续记录3天
            3L -> Condition.StreakDays(7)          // 连续记录7天
            4L -> Condition.StreakDays(14)         // 连续记录14天
            5L -> Condition.StreakDays(30)         // 连续记录30天
            
            // 记录数量成就
            6L -> Condition.TotalRecords(10)       // 记录达人
            7L -> Condition.TotalRecords(20)       // 记录大师
            8L -> Condition.TotalRecords(50)       // 记录王者
            9L -> Condition.TotalRecords(100)      // 记录传奇
            
            // 营养目标成就
            10L -> Condition.NutrientTarget("protein", 60.0)       // 蛋白质专家
            11L -> Condition.NutrientTarget("fiber", 25.0)         // 膳食纤维达人
            12L -> Condition.NutrientTarget("vitamin_c", 90.0)     // 维生素C冠军
            13L -> Condition.NutrientTarget("calcium", 1000.0)      // 钙质专家
            
            // 食物多样性成就
            14L -> Condition.FoodVariety(20)       // 食物探索家
            15L -> Condition.FoodVariety(30)       // 食物冒险家
            16L -> Condition.FoodVariety(50)       // 食物大师
            
            // 健康生活成就
            17L -> Condition.NutrientTarget("vegetables", 300.0)   // 蔬菜达人
            18L -> Condition.NutrientTarget("fruits", 200.0)       // 水果爱好者
            19L -> Condition.StreakDays(3)          // 饮水冠军
            20L -> Condition.StreakDays(7)          // 饮水大师
            21L -> Condition.StreakDays(30)         // 饮水传奇
            
            // 特殊成就
            22L -> Condition.TotalRecords(7)        // 营养均衡大师
            23L -> Condition.TotalRecords(3)        // 完美一天
            24L -> Condition.StreakDays(5)          // 完美一周
            25L -> Condition.TotalRecords(10)       // 成就收藏家
            26L -> Condition.TotalRecords(20)       // 成就大师
            
            // 默认条件
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