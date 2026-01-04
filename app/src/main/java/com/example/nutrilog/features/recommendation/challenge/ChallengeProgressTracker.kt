// app/src/main/java/com/nutrilog/features/recommendation/challenge/ChallengeProgressTracker.kt
package com.example.nutrilog.features.recommendation.challenge

import kotlinx.coroutines.flow.first

/**
 * 挑战进度追踪器
 */
class ChallengeProgressTracker(
    private val challengeRepository: ChallengeRepository
) {

    /**
     * 追踪挑战进度
     */
    suspend fun trackChallengeProgress(userId: Long) {
        val date = java.time.LocalDate.now().toString()
        val challenges = challengeRepository.getDailyChallenges(userId, date).first()

        challenges.forEach { challenge ->
            if (!challenge.completed) {
                val progress = calculateChallengeProgress(challenge)

                if (progress >= challenge.target) {
                    // 挑战完成
                    challengeRepository.markDailyChallengeCompleted(challenge.id)

                    // 发送完成通知（简化实现）
                    println("挑战完成: ${challenge.title}, 奖励: ${challenge.rewardPoints}积分")
                } else {
                    // 更新进度
                    challengeRepository.updateDailyChallengeProgress(challenge.id, progress)
                }
            }
        }
    }

    /**
     * 计算挑战进度
     */
    private fun calculateChallengeProgress(challenge: DailyChallenge): Float {
        return when (challenge.type) {
            ChallengeType.MANDATORY -> {
                // 是否记录了至少一餐（简化实现）
                0.5f // 模拟进度
            }

            ChallengeType.NUTRITION -> {
                val nutrient = challenge.metadata["nutrient"] as? String
                calculateNutrientProgress(nutrient, challenge.target)
            }

            ChallengeType.HABIT -> {
                val habit = challenge.metadata["habit"] as? String
                calculateHabitProgress(habit, challenge.target)
            }

            else -> {
                // 其他类型使用随机进度（简化实现）
                (0..100).random().toFloat()
            }
        }.coerceIn(0f, challenge.target)
    }

    /**
     * 计算营养进度（简化实现）
     */
    private fun calculateNutrientProgress(nutrient: String?, target: Float): Float {
        // 简化实现：返回模拟进度
        return when (nutrient) {
            "protein" -> 45.0f
            "fiber" -> 18.0f
            else -> 30.0f
        }
    }

    /**
     * 计算习惯进度（简化实现）
     */
    private fun calculateHabitProgress(habit: String?, target: Float): Float {
        // 简化实现：返回模拟进度
        return when (habit) {
            "early_breakfast" -> 1.0f  // 已完成
            "early_dinner" -> 0.5f     // 进行中
            "drink_water" -> 1500.0f   // 已喝1500ml
            else -> 0f
        }
    }
}