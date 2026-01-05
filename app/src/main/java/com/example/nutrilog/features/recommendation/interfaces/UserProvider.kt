// app/src/main/java/com/nutrilog/features/recommendation/interfaces/UserProvider.kt
package com.example.nutrilog.features.recommendation.interfaces

/**
 * 用户数据提供接口（D10将由全局UserService提供）
 */
interface UserProvider {
    suspend fun getUserPoints(userId: Long): Int
    suspend fun updateUserPoints(userId: Long, points: Int)
    suspend fun getUserLevel(userId: Long): Int
    suspend fun addUnlockedTitle(userId: Long, title: String)
}