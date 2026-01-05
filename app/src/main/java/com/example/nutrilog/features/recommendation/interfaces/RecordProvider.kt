// app/src/main/java/com/nutrilog/features/recommendation/interfaces/RecordProvider.kt
package com.example.nutrilog.features.recommendation.interfaces

import com.example.nutrilog.data.entities.MealRecord

/**
 * 记录数据提供接口（D10将由A同学实现）
 */
interface RecordProvider {
    suspend fun getUserRecords(userId: Long, days: Int): List<MealRecord>
    suspend fun getTodayRecords(userId: Long): List<MealRecord>
    suspend fun getStreakDays(userId: Long): Int
    suspend fun getFoodVarietyCount(userId: Long, days: Int): Int
}