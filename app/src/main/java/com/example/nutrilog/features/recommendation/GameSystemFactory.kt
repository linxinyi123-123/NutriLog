// app/src/main/java/com/nutrilog/features/recommendation/GameSystemFactory.kt
package com.example.nutrilog.features.recommendation

import com.example.nutrilog.features.recommendation.database.dao.AchievementDao
import com.example.nutrilog.features.recommendation.database.dao.ChallengeDao
import com.example.nutrilog.features.recommendation.gamification.AchievementRepository
import com.example.nutrilog.features.recommendation.gamification.AchievementUnlocker
import com.example.nutrilog.features.recommendation.gamification.AchievementRewardSystem
import com.example.nutrilog.features.recommendation.challenge.ChallengeRepository
import com.example.nutrilog.features.recommendation.challenge.ChallengeSystem
import com.example.nutrilog.features.recommendation.challenge.ChallengeProgressTracker
import com.example.nutrilog.features.recommendation.challenge.WeeklyChallengeSystem

/**
 * 游戏系统工厂 - 仅用于独立开发和测试
 * D10会重写这个工厂来集成真实服务
 */
object GameSystemFactory {

    /**
     * 创建游戏化组件（D8独立版本）
     */
    fun createGamificationSystem(achievementDao: AchievementDao): GamificationComponents {
        val achievementRepository = AchievementRepository(achievementDao)
        val achievementUnlocker = AchievementUnlocker(achievementRepository)
        val rewardSystem = AchievementRewardSystem()

        return GamificationComponents(
            achievementRepository = achievementRepository,
            achievementUnlocker = achievementUnlocker,
            rewardSystem = rewardSystem
        )
    }

    /**
     * 创建挑战系统组件（D9独立版本）
     */
    fun createChallengeSystem(challengeDao: ChallengeDao): ChallengeComponents {
        val challengeRepository = ChallengeRepository(challengeDao)
        val challengeSystem = ChallengeSystem(challengeRepository)
        val challengeProgressTracker = ChallengeProgressTracker(challengeRepository)
        val weeklyChallengeSystem = WeeklyChallengeSystem(challengeRepository)

        return ChallengeComponents(
            challengeRepository = challengeRepository,
            challengeSystem = challengeSystem,
            challengeProgressTracker = challengeProgressTracker,
            weeklyChallengeSystem = weeklyChallengeSystem
        )
    }
}

/**
 * 游戏化组件容器
 */
data class GamificationComponents(
    val achievementRepository: AchievementRepository,
    val achievementUnlocker: AchievementUnlocker,
    val rewardSystem: AchievementRewardSystem
)

/**
 * 挑战组件容器
 */
data class ChallengeComponents(
    val challengeRepository: ChallengeRepository,
    val challengeSystem: ChallengeSystem,
    val challengeProgressTracker: ChallengeProgressTracker,
    val weeklyChallengeSystem: WeeklyChallengeSystem
)