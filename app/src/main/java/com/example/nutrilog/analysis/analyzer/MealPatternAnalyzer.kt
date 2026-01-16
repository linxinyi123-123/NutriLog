package com.example.nutrilog.analysis.analyzer

import com.example.nutrilog.analysis.analysis.RegularityAnalysis
import com.example.nutrilog.analysis.analysis.TimeRegularity
import com.example.nutrilog.data.entities.MealType
import com.example.nutrilog.shared.MealRecord

class MealPatternAnalyzer {
    // 分析餐次规律性
    fun analyzeMealRegularity(records: List<MealRecord>): RegularityAnalysis {
        val mealTimes = records.map { it.time }

        // 计算早餐时间分布（7:00-9:00为理想）
        val breakfastRecords = records.filter { it.mealType == MealType.BREAKFAST }
        val breakfastRegularity = analyzeTimeRegularity(breakfastRecords, "07:00", "09:00")

        // 计算午餐时间分布（11:30-13:00）
        val lunchRecords = records.filter { it.mealType == MealType.LUNCH }
        val lunchRegularity = analyzeTimeRegularity(lunchRecords, "11:30", "13:00")

        // 计算晚餐时间分布（18:00-20:00）
        val dinnerRecords = records.filter { it.mealType == MealType.DINNER }
        val dinnerRegularity = analyzeTimeRegularity(dinnerRecords, "18:00", "20:00")

        // 分析夜宵频率（避免除以零）
        val lateNightRecords = records.filter { isLateNightMeal(it.time) }
        val lateNightFrequency = if (records.isEmpty()) {
            0.0
        } else {
            lateNightRecords.size.toDouble() / records.size
        }

        return RegularityAnalysis(
            breakfastScore = breakfastRegularity.score,
            lunchScore = lunchRegularity.score,
            dinnerScore = dinnerRegularity.score,
            lateNightFrequency = lateNightFrequency,
            suggestions = generateSuggestions(breakfastRegularity, lunchRegularity, dinnerRegularity)
        )
    }

    private fun analyzeTimeRegularity(
        records: List<MealRecord>,
        idealStart: String,
        idealEnd: String
    ): TimeRegularity {
        if (records.isEmpty()) return TimeRegularity(0.0, emptyList())

        val inRangeCount = records.count { isTimeInRange(it.time, idealStart, idealEnd) }
        val score = (inRangeCount.toDouble() / records.size) * 100

        return TimeRegularity(score, records.map { it.time })
    }

    private fun isTimeInRange(time: String, start: String, end: String): Boolean {
        val timeMinutes = timeToMinutes(time)
        val startMinutes = timeToMinutes(start)
        val endMinutes = timeToMinutes(end)
        return timeMinutes in startMinutes..endMinutes
    }

    private fun timeToMinutes(time: String): Int {
        val parts = time.split(":")
        return parts[0].toInt() * 60 + parts[1].toInt()
    }

    // 判断是否为夜宵时间（20:00之后到次日2:00之前）
    private fun isLateNightMeal(time: String): Boolean {
        val timeMinutes = timeToMinutes(time)
        val lateNightStart = timeToMinutes("20:00")
        val morningEnd = timeToMinutes("02:00")

        // 处理跨天的情况：22:00-24:00 或 00:00-06:00
        return timeMinutes >= lateNightStart || timeMinutes <= morningEnd
    }

    // 生成饮食规律建议
    private fun generateSuggestions(
        breakfastRegularity: TimeRegularity,
        lunchRegularity: TimeRegularity,
        dinnerRegularity: TimeRegularity
    ): List<String> {
        val suggestions = mutableListOf<String>()

        // 早餐建议
        when {
            breakfastRegularity.score < 50 -> {
                suggestions.add("您的早餐时间很不规律，建议在7:00-9:00之间固定时间吃早餐")
                suggestions.add("规律的早餐有助于启动新陈代谢，提高一天的能量水平")
            }
            breakfastRegularity.score < 80 -> {
                suggestions.add("您的早餐时间基本规律，可以进一步优化到7:00-9:00的理想时间段")
            }
            else -> {
                suggestions.add("恭喜！您的早餐时间非常规律，请继续保持")
            }
        }

        // 午餐建议
        when {
            lunchRegularity.score < 50 -> {
                suggestions.add("您的午餐时间很不规律，建议在11:30-13:00之间固定时间用餐")
                suggestions.add("规律的午餐时间有助于维持血糖稳定，避免下午疲劳")
            }
            lunchRegularity.score < 80 -> {
                suggestions.add("您的午餐时间基本规律，可以尝试更集中在11:30-13:00时段")
            }
            else -> {
                suggestions.add("您的午餐时间安排很棒，继续保持这个良好习惯")
            }
        }

        // 晚餐建议
        when {
            dinnerRegularity.score < 50 -> {
                suggestions.add("您的晚餐时间很不规律，建议在18:00-20:00之间完成晚餐")
                suggestions.add("较早的晚餐时间有利于消化和睡眠质量")
            }
            dinnerRegularity.score < 80 -> {
                suggestions.add("您的晚餐时间基本合理，可以稍微提前到18:00-20:00区间")
            }
            else -> {
                suggestions.add("您的晚餐时间安排非常健康，有利于身体恢复")
            }
        }

        // 综合建议
        val avgScore = (breakfastRegularity.score + lunchRegularity.score + dinnerRegularity.score) / 3
        when {
            avgScore < 40 -> {
                suggestions.add("您的整体饮食规律性较差，建议制定固定的三餐时间表并严格执行")
                suggestions.add("可以使用手机提醒功能来帮助建立规律的饮食习惯")
            }
            avgScore < 70 -> {
                suggestions.add("您的饮食规律性中等，继续优化各餐时间可以提高健康水平")
            }
            else -> {
                suggestions.add("您的饮食规律性非常好，这种习惯对长期健康很有益处")
            }
        }

        return suggestions
    }


}