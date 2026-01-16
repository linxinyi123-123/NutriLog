package com.example.nutrilog.analysis.analysis

data class EatingPatternAnalysis(
    val mealRegularity: Double,          // 餐次规律性得分
    val snackFrequency: Double,          // 零食频率
    val lateNightEating: Double,         // 夜宵频率
    val mealTimingConsistency: Double,   // 用餐时间一致性
    val identifiedPatterns: List<Pattern>,
    val suggestions: List<String>
)

data class Pattern(
    val name: String,        // 模式名称
    val confidence: Double,  // 置信度
    val description: String
)

// 常见饮食模式
enum class CommonPatterns {
    SKIP_BREAKFAST,          // 常跳过早餐
    LATE_DINNER,             // 晚餐时间过晚
    HIGH_SNACKING,           // 零食摄入过多
    LOW_VEGETABLE,           // 蔬菜摄入不足
    HIGH_PROCESSED_FOOD      // 加工食品过多
}