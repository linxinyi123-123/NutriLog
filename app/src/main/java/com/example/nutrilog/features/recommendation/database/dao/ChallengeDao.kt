// app/src/main/java/com/nutrilog/features/recommendation/database/dao/ChallengeDao.kt
package com.example.nutrilog.features.recommendation.database.dao

import androidx.room.*
import com.example.nutrilog.features.recommendation.database.entity.DailyChallengeEntity
import com.example.nutrilog.features.recommendation.database.entity.WeeklyChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

    // ==================== Daily Challenges ====================

    @Query("SELECT * FROM daily_challenges WHERE user_id = :userId AND date = :date ORDER BY created_at ASC")
    fun getDailyChallenges(userId: Long, date: String): Flow<List<DailyChallengeEntity>>

    @Query("SELECT * FROM daily_challenges WHERE user_id = :userId AND date = :date AND completed = 0")
    fun getIncompleteDailyChallenges(userId: Long, date: String): Flow<List<DailyChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyChallenge(challenge: DailyChallengeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDailyChallenges(challenges: List<DailyChallengeEntity>)

    @Update
    suspend fun updateDailyChallenge(challenge: DailyChallengeEntity)

    @Query("UPDATE daily_challenges SET progress = :progress WHERE id = :challengeId")
    suspend fun updateDailyChallengeProgress(challengeId: Long, progress: Float)

    @Query("UPDATE daily_challenges SET completed = 1, progress = target WHERE id = :challengeId")
    suspend fun markDailyChallengeCompleted(challengeId: Long)

    @Query("DELETE FROM daily_challenges WHERE date < :expireDate")
    suspend fun deleteExpiredDailyChallenges(expireDate: String)

    // ==================== Weekly Challenges ====================

    @Query("SELECT * FROM weekly_challenges WHERE user_id = :userId AND week_start_date = :weekStartDate")
    fun getWeeklyChallenges(userId: Long, weekStartDate: String): Flow<List<WeeklyChallengeEntity>>

    @Query("SELECT * FROM weekly_challenges WHERE user_id = :userId AND completed = 0 AND week_start_date >= :startDate")
    fun getActiveWeeklyChallenges(userId: Long, startDate: String): Flow<List<WeeklyChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyChallenge(challenge: WeeklyChallengeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWeeklyChallenges(challenges: List<WeeklyChallengeEntity>)

    @Update
    suspend fun updateWeeklyChallenge(challenge: WeeklyChallengeEntity)

    @Query("UPDATE weekly_challenges SET progress = :progress WHERE id = :challengeId")
    suspend fun updateWeeklyChallengeProgress(challengeId: Long, progress: Float)

    @Query("UPDATE weekly_challenges SET completed = 1 WHERE id = :challengeId")
    suspend fun markWeeklyChallengeCompleted(challengeId: Long)

    @Query("DELETE FROM weekly_challenges WHERE week_start_date < :expireDate")
    suspend fun deleteExpiredWeeklyChallenges(expireDate: String)

    // ==================== Statistics ====================

    @Query("SELECT COUNT(*) FROM daily_challenges WHERE user_id = :userId AND completed = 1")
    suspend fun getCompletedDailyChallengeCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM weekly_challenges WHERE user_id = :userId AND completed = 1")
    suspend fun getCompletedWeeklyChallengeCount(userId: Long): Int

    @Query("SELECT SUM(reward_points) FROM daily_challenges WHERE user_id = :userId AND completed = 1 AND date >= :startDate")
    suspend fun getTotalPointsFromDailyChallenges(userId: Long, startDate: String): Int?

    @Query("SELECT SUM(reward_points) FROM weekly_challenges WHERE user_id = :userId AND completed = 1 AND week_start_date >= :startDate")
    suspend fun getTotalPointsFromWeeklyChallenges(userId: Long, startDate: String): Int?
}