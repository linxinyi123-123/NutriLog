// app/src/main/java/com/nutrilog/features/recommendation/engine/rule/RecommendationRule.kt
package com.example.nutrilog.features.recommendation.engine.rule

import com.example.nutrilog.features.recommendation.model.Priority

data class RecommendationRule(
    val id: Long,
    val name: String,
    val type: RuleType,
    val condition: RuleCondition,
    val action: RuleAction,
    val priority: Priority,
    val message: String,
    val cooldown: Long = 0, // 冷却时间(毫秒)
    val expiration: Long? = null, // 过期时间
    val isActive: Boolean = true
)

enum class RuleType {
    NUTRITION_GAP,      // 营养缺口
    MEAL_PATTERN,       // 饮食模式
    HEALTH_SCORE,       // 健康评分
    GOAL_BASED,         // 基于目标
    CONTEXT_AWARE,      // 场景感知
    COMPOSITE           // 复合规则
}