package com.example.nutrilog.features.recommendation.model

import java.time.LocalTime

/**
 * 用餐时间相关工具类
 */
object MealTime {

    /**
     * 根据当前时间判断用餐类型
     */
    fun getMealTypeByTime(currentTime: LocalTime): String {
        return when {
            currentTime.isBetween(5, 0, 10, 0) -> "早餐"
            currentTime.isBetween(10, 30, 13, 30) -> "午餐"
            currentTime.isBetween(17, 0, 20, 30) -> "晚餐"
            else -> "加餐"
        }
    }

    /**
     * 判断是否为忙碌时间
     */
    fun isBusyTime(currentTime: LocalTime): Boolean {
        return when {
            // 工作日忙碌时间：早上7-9点，中午11:30-13:00，晚上18-19点
            currentTime.isBetween(7, 0, 9, 0) -> true
            currentTime.isBetween(11, 30, 13, 0) -> true
            currentTime.isBetween(18, 0, 19, 0) -> true
            else -> false
        }
    }

    /**
     * 判断是否为夜宵时间
     */
    fun isLateNightSnackTime(currentTime: LocalTime): Boolean {
        return currentTime.isBetween(20, 30, 23, 59) ||
                currentTime.isBetween(0, 0, 2, 0)
    }

    /**
     * 获取推荐的用餐时间窗口
     */
    fun getRecommendedMealWindow(mealType: String): Pair<LocalTime, LocalTime> {
        return when (mealType) {
            "早餐" -> LocalTime.of(7, 0) to LocalTime.of(9, 0)
            "午餐" -> LocalTime.of(11, 30) to LocalTime.of(13, 0)
            "晚餐" -> LocalTime.of(18, 0) to LocalTime.of(20, 0)
            else -> LocalTime.of(14, 30) to LocalTime.of(16, 30) // 下午加餐
        }
    }

    /**
     * 扩展函数：判断时间是否在范围内
     */
    private fun LocalTime.isBetween(startHour: Int, startMinute: Int,
                                    endHour: Int, endMinute: Int): Boolean {
        val start = LocalTime.of(startHour, startMinute)
        val end = LocalTime.of(endHour, endMinute)
        return (this == start || this.isAfter(start)) &&
                (this == end || this.isBefore(end))
    }
}