package com.example.nutrilog.features.recommendation.model.improvement

/**
 * 改善计划状态枚举
 */
enum class PlanStatus {
    DRAFT,          // 草稿
    ACTIVE,         // 进行中
    PAUSED,         // 已暂停
    COMPLETED,      // 已完成
    FAILED,         // 失败
    CANCELLED       // 已取消
}