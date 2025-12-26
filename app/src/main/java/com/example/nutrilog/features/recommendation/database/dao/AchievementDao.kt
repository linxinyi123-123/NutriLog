package com.example.nutrilog.features.recommendation.database.dao

import androidx.room.*
import com.example.nutrilog.features.recommendation.database.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements WHERE user_id = :userId")
    fun getUserAchievements(userId: Long): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE user_id = :userId AND is_unlocked = 1")
    fun getUnlockedAchievements(userId: Long): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE user_id = :userId AND is_unlocked = 0")
    fun getLockedAchievements(userId: Long): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("UPDATE achievements SET is_unlocked = 1, unlocked_at = :unlockedAt, progress = 1.0 WHERE id = :id")
    suspend fun unlockAchievement(id: Long, unlockedAt: Long)

    @Query("UPDATE achievements SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Float)

    @Query("DELETE FROM achievements WHERE id = :id")
    suspend fun deleteAchievement(id: Long)

    @Query("SELECT COUNT(*) FROM achievements WHERE user_id = :userId AND is_unlocked = 1")
    suspend fun getUnlockedCount(userId: Long): Int
}