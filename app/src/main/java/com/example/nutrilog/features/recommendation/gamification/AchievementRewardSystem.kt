// app/src/main/java/com/nutrilog/features/recommendation/gamification/AchievementRewardSystem.kt
package com.example.nutrilog.features.recommendation.gamification

import com.example.nutrilog.features.recommendation.model.gamification.Achievement

// 移除 @Inject 注解
class AchievementRewardSystem {

    suspend fun grantAchievementRewards(
        userId: Long,
        achievement: Achievement
    ): List<SpecialReward> {
        val rewards = mutableListOf<SpecialReward>()

        rewards.add(
            SpecialReward(
                type = RewardType.POINTS,
                value = achievement.points.toString(),
                description = "获得${achievement.points}积分"
            )
        )

        val specialRewards = checkSpecialRewards(achievement)
        rewards.addAll(specialRewards)

        return rewards
    }

    /**
     * 检查特殊奖励
     */
    private fun checkSpecialRewards(achievement: Achievement): List<SpecialReward> {
        return when (achievement.id) {
            // 连续记录7天成就
            2L -> listOf(
                SpecialReward(
                    type = RewardType.TITLE,
                    value = "记录达人",
                    description = "解锁'记录达人'头衔",
                    unlockedTitles = listOf("记录达人")
                )
            )

            // 饮食均衡一周成就
            4L -> listOf(
                SpecialReward(
                    type = RewardType.BADGE,
                    value = "balance_master",
                    description = "获得'均衡大师'徽章"
                ),
                SpecialReward(
                    type = RewardType.PREMIUM_FEATURE,
                    value = "advanced_analysis",
                    description = "解锁高级分析功能7天"
                )
            )

            // 尝试20种食物成就
            7L -> listOf(
                SpecialReward(
                    type = RewardType.TITLE,
                    value = "美食探索家",
                    description = "解锁'美食探索家'头衔",
                    unlockedTitles = listOf("美食探索家")
                )
            )

            else -> emptyList()
        }
    }

    /**
     * 授予等级奖励
     */
    suspend fun grantLevelRewards(userId: Long, level: Int): List<SpecialReward> {
        val rewards = mutableListOf<SpecialReward>()

        when (level) {
            3 -> rewards.add(
                SpecialReward(
                    type = RewardType.PREMIUM_FEATURE,
                    value = "export_reports",
                    description = "解锁报告导出功能"
                )
            )
            5 -> rewards.add(
                SpecialReward(
                    type = RewardType.TITLE,
                    value = "健康先锋",
                    description = "解锁'健康先锋'头衔",
                    unlockedTitles = listOf("健康先锋")
                )
            )
            8 -> rewards.add(
                SpecialReward(
                    type = RewardType.PREMIUM_FEATURE,
                    value = "custom_plans",
                    description = "解锁自定义计划功能"
                )
            )
            10 -> rewards.addAll(
                listOf(
                    SpecialReward(
                        type = RewardType.TITLE,
                        value = "营养大师",
                        description = "解锁'营养大师'头衔",
                        unlockedTitles = listOf("营养大师")
                    ),
                    SpecialReward(
                        type = RewardType.BADGE,
                        value = "master_badge",
                        description = "获得大师徽章"
                    )
                )
            )
        }

        return rewards
    }
}