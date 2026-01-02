// app/src/main/java/com/nutrilog/features/recommendation/gamification/SpecialReward.kt
package com.example.nutrilog.features.recommendation.gamification

/**
 * 特殊奖励数据模型
 */
data class SpecialReward(
    val type: RewardType,           // 奖励类型
    val value: String,              // 奖励值（如头衔名称、徽章ID）
    val description: String,        // 奖励描述
    val unlockedTitles: List<String> = emptyList() // 解锁的头衔列表
)