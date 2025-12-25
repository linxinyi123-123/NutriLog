package com.example.nutrilog.features.recommendation.database.dao

import androidx.room.*
import com.example.nutrilog.features.recommendation.database.entity.HealthGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthGoalDao {
    @Query("SELECT * FROM health_goals WHERE user_id = :userId AND status = 'ACTIVE'")
    fun getActiveGoals(userId: Long): Flow<List<HealthGoalEntity>>

    @Query("SELECT * FROM health_goals WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllGoals(userId: Long): Flow<List<HealthGoalEntity>>

    @Insert
    suspend fun insertGoal(goal: HealthGoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goals: List<HealthGoalEntity>)

    @Update
    suspend fun updateGoal(goal: HealthGoalEntity)

    @Query("UPDATE health_goals SET progress = :progress WHERE id = :goalId")
    suspend fun updateProgress(goalId: Long, progress: Float)

    @Query("UPDATE health_goals SET status = :status WHERE id = :goalId")
    suspend fun updateStatus(goalId: Long, status: String)

    @Query("DELETE FROM health_goals WHERE id = :goalId")
    suspend fun deleteGoal(goalId: Long)

    @Query("SELECT * FROM health_goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Long): HealthGoalEntity?
}