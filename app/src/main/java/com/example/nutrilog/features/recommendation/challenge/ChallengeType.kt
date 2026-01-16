// app/src/main/java/com/nutrilog/features/recommendation/challenge/ChallengeType.kt
package com.example.nutrilog.features.recommendation.challenge

/**
 * 挑战类型枚举
 */
enum class ChallengeType {
    MANDATORY,      // 必选挑战（如记录饮食）
    NUTRITION,      // 营养挑战（如增加蛋白质摄入）
    HABIT,          // 习惯挑战（如定时吃饭）
    VARIETY,        // 多样性挑战（如尝试新食物）
    REGULARITY,     // 规律性挑战（如按时三餐）
    EXPLORATION  ,   // 探索挑战（如尝试新餐厅）
    FOOD_VARIETY,
    MEAL_RECORD,
    HYDRATION,
    STREAK

}