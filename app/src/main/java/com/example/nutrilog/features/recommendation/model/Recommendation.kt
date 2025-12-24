// app/src/main/java/com/nutrilog/features/recommendation/model/Recommendation.kt
package com.nutrilog.features.recommendation.model

import java.util.Date

data class Recommendation(
    val id: Long,
    val type: RecommendationType,      // 推荐类型
    val title: String,                // 标题
    val description: String,          // 描述
    val priority: Priority,           // 优先级
    val confidence: Float,            // 置信度(0-1)
    val reason: String,               // 推荐理由
    val actions: List<Action>,        // 可执行操作
    val expiration: Date? = null,     // 过期时间
    val metadata: Map<String, Any> = emptyMap() // 扩展数据
)

enum class RecommendationType {
    NUTRITION_GAP,      // 营养缺口
    MEAL_PLAN,          // 饮食计划
    FOOD_SUGGESTION,    // 食物推荐
    HABIT_IMPROVEMENT,  // 习惯改进
    EDUCATIONAL         // 知识教育
}

enum class Priority {
    HIGH,    // 立即处理
    MEDIUM,  // 建议处理
    LOW      // 可选处理
}

// 可执行操作（简化版）
sealed class Action {
    data class ShowFoodDetails(val foodId: Long) : Action()
    data class AddToMealPlan(val foodIds: List<Long>) : Action()
    data class DismissRecommendation(val reason: String? = null) : Action()
    // 根据实际需要添加更多操作
}