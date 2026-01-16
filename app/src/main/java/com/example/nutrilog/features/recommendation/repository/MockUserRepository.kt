// app/src/main/java/com/nutrilog/features/recommendation/repository/MockUserRepository.kt
package com.example.nutrilog.features.recommendation.repository

import com.example.nutrilog.features.recommendation.gamification.UserStats

/**
 * 模拟用户仓库实现（用于测试和独立开发）
 */
class MockUserRepository : UserRepository {

    private val users = mutableMapOf<Long, User>()

    init {
        // 初始化一个测试用户
        users[1] = User(
            id = 1,
            name = "测试用户",
            email = "test@example.com",
            totalPoints = 150,
            level = 3,
            unlockedTitles = listOf("记录达人")
        )
    }

    override suspend fun getUser(userId: Long): User? {
        return users[userId]
    }

    override suspend fun updateUserStats(userId: Long, stats: UserStats) {
        val user = users[userId] ?: return
        users[userId] = user.copy(
            totalPoints = stats.totalPoints,
            level = stats.level
        )
        println("更新用户统计: userId=$userId, stats=$stats")
    }

    override suspend fun addPoints(userId: Long, points: Int) {
        val user = users[userId] ?: return
        users[userId] = user.copy(totalPoints = user.totalPoints + points)
        println("用户增加积分: userId=$userId, points=$points, 总积分=${user.totalPoints + points}")
    }

    override suspend fun updateLevel(userId: Long, level: Int) {
        val user = users[userId] ?: return
        users[userId] = user.copy(level = level)
        println("用户等级更新: userId=$userId, level=$level")
    }

    override suspend fun updateUserTitles(userId: Long, newTitles: List<String>) {
        val user = users[userId] ?: return
        val currentTitles = user.unlockedTitles.toMutableSet()
        currentTitles.addAll(newTitles)
        users[userId] = user.copy(unlockedTitles = currentTitles.toList())
        println("用户头衔更新: userId=$userId, 新头衔=$newTitles")
    }


}