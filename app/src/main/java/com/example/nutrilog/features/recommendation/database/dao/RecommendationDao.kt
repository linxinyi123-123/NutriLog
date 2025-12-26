package com.example.nutrilog.features.recommendation.database.dao

import androidx.room.*
import com.example.nutrilog.features.recommendation.database.entity.RecommendationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDao {
    @Query("SELECT * FROM recommendations WHERE user_id = :userId AND is_read = 0 ORDER BY priority DESC, created_at DESC")
    fun getUnreadRecommendations(userId: Long): Flow<List<RecommendationEntity>>

    @Query("SELECT * FROM recommendations WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllRecommendations(userId: Long): Flow<List<RecommendationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendation(recommendation: RecommendationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recommendations: List<RecommendationEntity>)

    @Update
    suspend fun updateRecommendation(recommendation: RecommendationEntity)

    @Query("UPDATE recommendations SET is_read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE recommendations SET is_applied = 1 WHERE id = :id")
    suspend fun markAsApplied(id: Long)

    @Query("DELETE FROM recommendations WHERE expires_at < :currentTime AND expires_at IS NOT NULL")
    suspend fun deleteExpiredRecommendations(currentTime: Long)

    @Query("DELETE FROM recommendations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM recommendations WHERE user_id = :userId AND is_read = 0")
    suspend fun getUnreadCount(userId: Long): Int
}