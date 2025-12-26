// app/src/main/java/com/nutrilog/features/recommendation/database/dao/RecommendationRuleDao.kt
package com.example.nutrilog.features.recommendation.database.dao

import androidx.room.*
import com.example.nutrilog.features.recommendation.database.entity.RecommendationRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationRuleDao {

    @Query("SELECT * FROM recommendation_rules WHERE id = :id")
    suspend fun getRuleById(id: Long): RecommendationRuleEntity?

    @Query("SELECT * FROM recommendation_rules")
    suspend fun getAllRules(): List<RecommendationRuleEntity>

    @Query("SELECT * FROM recommendation_rules WHERE type = :type")
    fun getRulesByType(type: String): Flow<List<RecommendationRuleEntity>>

    @Query("SELECT * FROM recommendation_rules WHERE type IN (:types)")
    suspend fun getRulesByTypes(types: List<String>): List<RecommendationRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: RecommendationRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<RecommendationRuleEntity>)

    @Update
    suspend fun updateRule(rule: RecommendationRuleEntity)

    @Query("DELETE FROM recommendation_rules WHERE id = :id")
    suspend fun deleteRule(id: Long)

    @Query("DELETE FROM recommendation_rules")
    suspend fun deleteAllRules()

    @Query("SELECT COUNT(*) FROM recommendation_rules")
    suspend fun getRuleCount(): Int
}