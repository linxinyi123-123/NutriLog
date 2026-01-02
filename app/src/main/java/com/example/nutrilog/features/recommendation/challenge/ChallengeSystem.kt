// app/src/main/java/com/nutrilog/features/recommendation/challenge/ChallengeSystem.kt
package com.example.nutrilog.features.recommendation.challenge

import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * 挑战生成系统
 */
class ChallengeSystem(
    private val challengeRepository: ChallengeRepository
) {

    /**
     * 获取今日挑战
     */
    suspend fun getDailyChallenges(userId: Long): List<DailyChallenge> {
        val date = LocalDate.now().toString()

        // 检查是否已有今日挑战
        val existingChallenges = challengeRepository.getDailyChallenges(userId, date).first()
        if (existingChallenges.isNotEmpty()) {
            return existingChallenges
        }

        // 生成新的每日挑战
        val newChallenges = generateDailyChallenges(userId, date)
        challengeRepository.saveAllDailyChallenges(newChallenges)

        return newChallenges
    }

    /**
     * 生成每日挑战
     */
    private fun generateDailyChallenges(
        userId: Long,
        date: String
    ): List<DailyChallenge> {
        val challenges = mutableListOf<DailyChallenge>()

        // 1. 必选挑战（记录饮食）
        challenges.add(
            DailyChallenge(
                id = generateChallengeId(),
                userId = userId,
                date = date,
                title = "完成今日记录",
                description = "记录今天的所有饮食",
                type = ChallengeType.MANDATORY,
                rewardPoints = 5,
                difficulty = ChallengeDifficulty.EASY,
                progress = 0f,
                target = 1f,
                unit = "次",
                completed = false,
                metadata = mapOf(
                    "action" to "record_meal",
                    "is_required" to true
                )
            )
        )

        // 2. 营养相关挑战
        val nutritionalChallenges = generateNutritionalChallenges(userId, date)
        challenges.addAll(nutritionalChallenges)

        // 3. 习惯相关挑战
        val habitChallenges = generateHabitChallenges(userId, date)
        challenges.addAll(habitChallenges)

        // 4. 随机挑战
        val randomChallenge = generateRandomChallenge(userId, date)
        randomChallenge?.let { challenges.add(it) }

        return challenges
    }

    /**
     * 生成营养相关挑战
     */
    private fun generateNutritionalChallenges(
        userId: Long,
        date: String
    ): List<DailyChallenge> {
        // 简化实现：生成固定的营养挑战
        // 实际实现需要基于用户营养缺口数据
        return listOf(
            DailyChallenge(
                id = generateChallengeId(),
                userId = userId,
                date = date,
                title = "增加蛋白质摄入",
                description = "今日摄入至少70g蛋白质",
                type = ChallengeType.NUTRITION,
                rewardPoints = 15,
                difficulty = ChallengeDifficulty.MEDIUM,
                progress = 0f,
                target = 70f,
                unit = "g",
                completed = false,
                metadata = mapOf(
                    "nutrient" to "protein",
                    "target" to 70.0
                )
            ),
            DailyChallenge(
                id = generateChallengeId(),
                userId = userId,
                date = date,
                title = "摄入足量蔬菜",
                description = "今日至少摄入300g蔬菜",
                type = ChallengeType.NUTRITION,
                rewardPoints = 10,
                difficulty = ChallengeDifficulty.EASY,
                progress = 0f,
                target = 300f,
                unit = "g",
                completed = false,
                metadata = mapOf(
                    "food_type" to "vegetable",
                    "target" to 300.0
                )
            )
        )
    }

    /**
     * 生成习惯相关挑战
     */
    private fun generateHabitChallenges(
        userId: Long,
        date: String
    ): List<DailyChallenge> {
        // 简化实现：生成固定的习惯挑战
        return listOf(
            DailyChallenge(
                id = generateChallengeId(),
                userId = userId,
                date = date,
                title = "早餐不迟到",
                description = "在9点前吃早餐",
                type = ChallengeType.HABIT,
                rewardPoints = 8,
                difficulty = ChallengeDifficulty.MEDIUM,
                progress = 0f,
                target = 1f,
                unit = "次",
                completed = false,
                metadata = mapOf(
                    "habit" to "early_breakfast",
                    "time_limit" to "09:00"
                )
            ),
            DailyChallenge(
                id = generateChallengeId(),
                userId = userId,
                date = date,
                title = "晚餐不过晚",
                description = "在20点前完成晚餐",
                type = ChallengeType.HABIT,
                rewardPoints = 8,
                difficulty = ChallengeDifficulty.MEDIUM,
                progress = 0f,
                target = 1f,
                unit = "次",
                completed = false,
                metadata = mapOf(
                    "habit" to "early_dinner",
                    "time_limit" to "20:00"
                )
            )
        )
    }

    /**
     * 生成随机挑战
     */
    private fun generateRandomChallenge(
        userId: Long,
        date: String
    ): DailyChallenge? {
        // 50%概率生成一个随机挑战
        if ((0..1).random() == 0) {
            val randomChallenges = listOf(
                DailyChallenge(
                    id = generateChallengeId(),
                    userId = userId,
                    date = date,
                    title = "尝试新食物",
                    description = "今天尝试一种你从未吃过的食物",
                    type = ChallengeType.EXPLORATION,
                    rewardPoints = 20,
                    difficulty = ChallengeDifficulty.HARD,
                    progress = 0f,
                    target = 1f,
                    unit = "种",
                    completed = false,
                    metadata = mapOf(
                        "category" to "new_food",
                        "exploration" to true
                    )
                ),
                DailyChallenge(
                    id = generateChallengeId(),
                    userId = userId,
                    date = date,
                    title = "喝水达标",
                    description = "今天喝至少2000ml水",
                    type = ChallengeType.HABIT,
                    rewardPoints = 12,
                    difficulty = ChallengeDifficulty.MEDIUM,
                    progress = 0f,
                    target = 2000f,
                    unit = "ml",
                    completed = false,
                    metadata = mapOf(
                        "habit" to "drink_water",
                        "target_ml" to 2000
                    )
                )
            )

            return randomChallenges.random()
        }

        return null
    }

    /**
     * 生成挑战ID（简化实现）
     */
    private fun generateChallengeId(): Long {
        return System.currentTimeMillis() + (0..999).random()
    }
}