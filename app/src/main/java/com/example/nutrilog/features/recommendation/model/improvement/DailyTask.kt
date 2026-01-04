package com.example.nutrilog.features.recommendation.model.improvement

import java.time.LocalDate

/**
 * 每日任务
 */
data class DailyTask(
    val id: String,                    // 任务ID
    val title: String,                 // 任务标题
    val description: String,           // 任务描述
    val type: TaskType,                // 任务类型
    val isRequired: Boolean = true,    // 是否必需
    val targetValue: Double? = null,   // 目标值
    val unit: String? = null,          // 单位
    val completed: Boolean = false,    // 是否完成
    val completedAt: LocalDate? = null, // 完成时间
    val notes: String? = null,         // 备注
    val difficulty: TaskDifficulty = TaskDifficulty.MEDIUM // 难度
) {
    /**
     * 获取任务进度（0-1）
     */
    fun getProgress(currentValue: Double? = null): Float {
        return when {
            completed -> 1.0f
            targetValue != null && currentValue != null ->
                (currentValue / targetValue).toFloat().coerceIn(0f, 1f)
            else -> 0f
        }
    }
}

/**
 * 任务类型
 */
enum class TaskType {
    NUTRITION,      // 营养相关
    HABIT,          // 习惯相关
    EXERCISE,       // 运动相关
    RECORDING,      // 记录相关
    EDUCATION,      // 教育学习
    OTHER           // 其他
}

/**
 * 任务难度
 */
enum class TaskDifficulty {
    EASY,       // 简单
    MEDIUM,     // 中等
    HARD        // 困难
}