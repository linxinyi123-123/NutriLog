package com.example.nutrilog.features.recommendation.model.improvement

import com.example.nutrilog.features.recommendation.model.GoalType

/**
 * 周计划
 */
data class WeeklyPlan(
    val weekNumber: Int,               // 第几周
    val focus: String,                 // 本周重点
    val description: String,           // 本周描述
    val targets: WeeklyTargets,        // 本周目标
    val dailyTasks: List<DailyTask>,   // 每日任务
    val successCriteria: List<String>, // 成功标准
    val tips: List<String> = emptyList() // 小贴士
) {
    /**
     * 获取本周进度
     */
    fun getProgress(completedTasks: List<String>): Float {
        if (dailyTasks.isEmpty()) return 0f
        val requiredTasks = dailyTasks.filter { it.isRequired }
        if (requiredTasks.isEmpty()) {
            val completedCount = dailyTasks.count { it.id in completedTasks }
            return completedCount.toFloat() / dailyTasks.size
        } else {
            val completedRequired = requiredTasks.count { it.id in completedTasks }
            return completedRequired.toFloat() / requiredTasks.size
        }
    }
}

/**
 * 每周目标
 */
data class WeeklyTargets(
    val calories: Double? = null,          // 热量目标
    val protein: Double? = null,           // 蛋白质目标
    val carbs: Double? = null,             // 碳水化合物目标
    val fat: Double? = null,               // 脂肪目标
    val fiber: Double? = null,             // 纤维目标
    val vegetables: Int? = null,           // 蔬菜份数
    val fruits: Int? = null,               // 水果份数
    val water: Double? = null,             // 水摄入量（升）
    val mealsRecorded: Int? = null,        // 记录餐次数
    val foodVariety: Int? = null           // 食物种类数
) {
    /**
     * 获取目标描述
     */
    fun getDescription(): String {
        val parts = mutableListOf<String>()

        calories?.let { parts.add("热量: ${it.toInt()}kcal") }
        protein?.let { parts.add("蛋白质: ${it}g") }
        fiber?.let { parts.add("纤维: ${it}g") }
        vegetables?.let { parts.add("蔬菜: ${it}份") }
        water?.let { parts.add("饮水: ${it}L") }

        return parts.joinToString(", ")
    }
}