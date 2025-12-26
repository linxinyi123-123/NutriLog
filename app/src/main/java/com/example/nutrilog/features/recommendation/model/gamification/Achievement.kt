// app/src/main/java/com/nutrilog/features/recommendation/model/gamification/Achievement.kt
package com.example.nutrilog.features.recommendation.model.gamification

data class Achievement(
    val id: Long,
    val name: String,            // 成就名称
    val description: String,     // 成就描述
    val type: AchievementType,   // 成就类型
    val icon: String,           // 图标资源
    val points: Int,            // 奖励积分
    val condition: Condition,   // 解锁条件
    val unlockedAt: Long? = null // 解锁时间
)

enum class AchievementType {
    DAILY,      // 日常成就
    MILESTONE,  // 里程碑成就
    SPECIAL,    // 特殊成就
    SECRET      // 隐藏成就
}

sealed class Condition {
    data class StreakDays(val days: Int) : Condition()
    data class TotalRecords(val count: Int) : Condition()
    data class NutrientTarget(val nutrient: String, val target: Double) : Condition()
    data class FoodVariety(val categories: Int) : Condition()
    data class Composite(val conditions: List<Condition>) : Condition()
}