// app/src/main/java/com/nutrilog/features/recommendation/challenge/ChallengeRepository.kt
package com.example.nutrilog.features.recommendation.challenge

import com.example.nutrilog.features.recommendation.database.dao.ChallengeDao
import com.example.nutrilog.features.recommendation.database.entity.DailyChallengeEntity
import com.example.nutrilog.features.recommendation.database.entity.WeeklyChallengeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * 挑战数据仓库
 */
class ChallengeRepository(
    private val challengeDao: ChallengeDao
) {

    // ==================== Daily Challenges ====================

    fun getDailyChallenges(userId: Long, date: String): Flow<List<DailyChallenge>> {
        return challengeDao.getDailyChallenges(userId, date).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getTodayChallenges(userId: Long): Flow<List<DailyChallenge>> {
        val today = LocalDate.now().toString()
        return getDailyChallenges(userId, today)
    }

    suspend fun saveDailyChallenge(challenge: DailyChallenge): Long {
        return challengeDao.insertDailyChallenge(challenge.toEntity())
    }

    suspend fun saveAllDailyChallenges(challenges: List<DailyChallenge>) {
        val entities = challenges.map { it.toEntity() }
        challengeDao.insertAllDailyChallenges(entities)
    }

    suspend fun updateDailyChallengeProgress(challengeId: Long, progress: Float) {
        challengeDao.updateDailyChallengeProgress(challengeId, progress)
    }

    suspend fun markDailyChallengeCompleted(challengeId: Long) {
        challengeDao.markDailyChallengeCompleted(challengeId)
    }

    // ==================== Weekly Challenges ====================

    fun getWeeklyChallenges(userId: Long, weekStartDate: String): Flow<List<WeeklyChallenge>> {
        return challengeDao.getWeeklyChallenges(userId, weekStartDate).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getCurrentWeekChallenges(userId: Long): Flow<List<WeeklyChallenge>> {
        val weekStart = getCurrentWeekStartDate()
        return getWeeklyChallenges(userId, weekStart)
    }

    suspend fun saveWeeklyChallenge(challenge: WeeklyChallenge): Long {
        return challengeDao.insertWeeklyChallenge(challenge.toEntity())
    }

    suspend fun saveAllWeeklyChallenges(challenges: List<WeeklyChallenge>) {
        val entities = challenges.map { it.toEntity() }
        challengeDao.insertAllWeeklyChallenges(entities)
    }

    suspend fun updateWeeklyChallengeProgress(challengeId: Long, progress: Float) {
        challengeDao.updateWeeklyChallengeProgress(challengeId, progress)
    }

    suspend fun markWeeklyChallengeCompleted(challengeId: Long) {
        challengeDao.markWeeklyChallengeCompleted(challengeId)
    }

    // ==================== Statistics ====================

    suspend fun getUserChallengeStats(userId: Long): ChallengeStats {
        val today = LocalDate.now().toString()
        val weekStart = getCurrentWeekStartDate()
        val monthStart = LocalDate.now().withDayOfMonth(1).toString()

        val completedDailyCount = challengeDao.getCompletedDailyChallengeCount(userId)
        val completedWeeklyCount = challengeDao.getCompletedWeeklyChallengeCount(userId)
        val dailyPoints = challengeDao.getTotalPointsFromDailyChallenges(userId, monthStart) ?: 0
        val weeklyPoints = challengeDao.getTotalPointsFromWeeklyChallenges(userId, monthStart) ?: 0

        return ChallengeStats(
            userId = userId,
            totalCompletedDaily = completedDailyCount,
            totalCompletedWeekly = completedWeeklyCount,
            totalPointsThisMonth = dailyPoints + weeklyPoints,
            currentStreak = calculateCurrentStreak(userId)
        )
    }

    // ==================== Helper Methods ====================

    private fun getCurrentWeekStartDate(): String {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.value // 1 = Monday, 7 = Sunday
        val startOfWeek = today.minusDays((dayOfWeek - 1).toLong())
        return startOfWeek.toString()
    }

    private suspend fun calculateCurrentStreak(userId: Long): Int {
        // 简化实现：计算连续完成挑战的天数
        // 实际实现需要查询数据库中的完成记录
        return 0
    }

    // ==================== Entity Conversion ====================

    private fun DailyChallengeEntity.toDomainModel(): DailyChallenge {
        return DailyChallenge(
            id = this.id,
            userId = this.userId,
            date = this.date,
            title = this.title,
            description = this.description,
            type = ChallengeType.valueOf(this.type),
            rewardPoints = this.rewardPoints,
            difficulty = ChallengeDifficulty.valueOf(this.difficulty),
            progress = this.progress,
            target = this.target,
            unit = this.unit,
            completed = this.completed,
            metadata = parseMetadata(this.metadata)
        )
    }

    private fun DailyChallenge.toEntity(): DailyChallengeEntity {
        return DailyChallengeEntity(
            id = this.id,
            userId = this.userId,
            date = this.date,
            title = this.title,
            description = this.description,
            type = this.type.name,
            rewardPoints = this.rewardPoints,
            difficulty = this.difficulty.name,
            progress = this.progress,
            target = this.target,
            unit = this.unit,
            completed = this.completed,
            metadata = serializeMetadata(this.metadata)
        )
    }

    private fun WeeklyChallengeEntity.toDomainModel(): WeeklyChallenge {
        return WeeklyChallenge(
            id = this.id,
            userId = this.userId,
            weekStartDate = this.weekStartDate,
            title = this.title,
            description = this.description,
            type = ChallengeType.valueOf(this.type),
            rewardPoints = this.rewardPoints,
            difficulty = ChallengeDifficulty.valueOf(this.difficulty),
            progress = this.progress,
            target = this.target,
            unit = this.unit,
            completed = this.completed,
            metadata = parseMetadata(this.metadata)
        )
    }

    private fun WeeklyChallenge.toEntity(): WeeklyChallengeEntity {
        return WeeklyChallengeEntity(
            id = this.id,
            userId = this.userId,
            weekStartDate = this.weekStartDate,
            title = this.title,
            description = this.description,
            type = this.type.name,
            rewardPoints = this.rewardPoints,
            difficulty = this.difficulty.name,
            progress = this.progress,
            target = this.target,
            unit = this.unit,
            completed = this.completed,
            metadata = serializeMetadata(this.metadata)
        )
    }

    private fun parseMetadata(json: String): Map<String, Any> {
        // 简化实现：返回空map
        // 实际实现需要使用JSON解析库
        return emptyMap()
    }

    private fun serializeMetadata(metadata: Map<String, Any>): String {
        // 简化实现：返回空字符串
        // 实际实现需要使用JSON序列化库
        return ""
    }
}

/**
 * 挑战统计数据
 */
data class ChallengeStats(
    val userId: Long,
    val totalCompletedDaily: Int,
    val totalCompletedWeekly: Int,
    val totalPointsThisMonth: Int,
    val currentStreak: Int
)