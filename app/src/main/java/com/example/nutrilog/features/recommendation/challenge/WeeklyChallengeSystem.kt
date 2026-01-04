// app/src/main/java/com/nutrilog/features/recommendation/challenge/WeeklyChallengeSystem.kt
package com.example.nutrilog.features.recommendation.challenge

import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * 每周挑战系统
 */
class WeeklyChallengeSystem(
    private val challengeRepository: ChallengeRepository
) {

    /**
     * 获取本周挑战
     */
    suspend fun getWeeklyChallenges(userId: Long): List<WeeklyChallenge> {
        val weekStartDate = getCurrentWeekStartDate()

        // 检查是否已有本周挑战
        val existingChallenges = challengeRepository.getWeeklyChallenges(userId, weekStartDate).first()
        if (existingChallenges.isNotEmpty()) {
            return existingChallenges
        }

        // 生成新的每周挑战
        val newChallenges = generateWeeklyChallenges(userId, weekStartDate)
        challengeRepository.saveAllWeeklyChallenges(newChallenges)

        return newChallenges
    }

    /**
     * 生成每周挑战
     */
    private fun generateWeeklyChallenges(
        userId: Long,
        weekStartDate: String
    ): List<WeeklyChallenge> {
        return listOf(
            WeeklyChallenge(
                id = generateWeeklyChallengeId(),
                userId = userId,
                weekStartDate = weekStartDate,
                title = "蔬菜多样化",
                description = "本周尝试至少10种不同的蔬菜",
                type = ChallengeType.VARIETY,
                rewardPoints = 50,
                difficulty = ChallengeDifficulty.MEDIUM,
                progress = 0f,
                target = 10f,
                unit = "种",
                completed = false,
                metadata = mapOf(
                    "category" to "vegetable_variety",
                    "weekNumber" to 1
                )
            ),
            WeeklyChallenge(
                id = generateWeeklyChallengeId(),
                userId = userId,
                weekStartDate = weekStartDate,
                title = "规律三餐",
                description = "本周至少有5天按时吃三餐",
                type = ChallengeType.REGULARITY,
                rewardPoints = 40,
                difficulty = ChallengeDifficulty.MEDIUM,
                progress = 0f,
                target = 5f,
                unit = "天",
                completed = false,
                metadata = mapOf(
                    "habit" to "regular_meals",
                    "weekNumber" to 1
                )
            ),
            WeeklyChallenge(
                id = generateWeeklyChallengeId(),
                userId = userId,
                weekStartDate = weekStartDate,
                title = "控制添加糖",
                description = "本周每日添加糖不超过25g",
                type = ChallengeType.NUTRITION,
                rewardPoints = 60,
                difficulty = ChallengeDifficulty.HARD,
                progress = 0f,
                target = 7f, // 7天都达标
                unit = "天",
                completed = false,
                metadata = mapOf(
                    "nutrient" to "sugar",
                    "target_daily" to 25.0
                )
            )
        )
    }

    /**
     * 追踪每周进度
     */
    suspend fun trackWeeklyProgress(userId: Long) {
        val weekStartDate = getCurrentWeekStartDate()

        // 获取本周挑战
        val challenges = challengeRepository.getWeeklyChallenges(userId, weekStartDate).first()

        challenges.forEach { challenge ->
            val progress = calculateWeeklyProgress(challenge)
            challengeRepository.updateWeeklyChallengeProgress(challenge.id, progress)

            if (progress >= challenge.target && !challenge.completed) {
                challengeRepository.markWeeklyChallengeCompleted(challenge.id)

                // 发送完成通知（简化实现）
                println("每周挑战完成: ${challenge.title}, 奖励: ${challenge.rewardPoints}积分")
            }
        }
    }

    /**
     * 计算每周进度
     */
    private fun calculateWeeklyProgress(challenge: WeeklyChallenge): Float {
        return when (challenge.type) {
            ChallengeType.VARIETY -> {
                // 计算不同蔬菜种类（简化实现）
                6.0f // 模拟已尝试6种蔬菜
            }

            ChallengeType.REGULARITY -> {
                // 计算按时三餐的天数（简化实现）
                3.0f // 模拟有3天按时三餐
            }

            ChallengeType.NUTRITION -> {
                // 计算添加糖控制达标天数（简化实现）
                4.0f // 模拟有4天达标
            }

            else -> 0f
        }
    }

    /**
     * 获取当前周的开始日期
     */
    private fun getCurrentWeekStartDate(): String {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.value // 1 = Monday, 7 = Sunday
        val startOfWeek = today.minusDays((dayOfWeek - 1).toLong())
        return startOfWeek.toString()
    }

    /**
     * 生成每周挑战ID（简化实现）
     */
    private fun generateWeeklyChallengeId(): Long {
        return System.currentTimeMillis() + (1000..1999).random()
    }
}