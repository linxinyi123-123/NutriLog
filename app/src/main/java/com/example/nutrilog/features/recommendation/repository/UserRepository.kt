// app/src/main/java/com/nutrilog/features/recommendation/repository/UserRepository.kt
package com.example.nutrilog.features.recommendation.repository
import com.example.nutrilog.features.recommendation.gamification.UserStats

/**
 * 用户仓库接口（模拟实现）
 */
interface UserRepository {
    suspend fun getUser(userId: Long): User?
    suspend fun updateUserStats(userId: Long, stats: UserStats)
    suspend fun addPoints(userId: Long, points: Int)
    suspend fun updateLevel(userId: Long, level: Int)
    suspend fun updateUserTitles(userId: Long, newTitles: List<String>)
}

/**
 * 用户数据模型（简化）
 */
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val totalPoints: Int = 0,
    val level: Int = 0,
    val unlockedTitles: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)