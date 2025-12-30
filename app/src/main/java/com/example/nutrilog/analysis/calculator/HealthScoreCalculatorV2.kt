package com.example.nutrilog.analysis.calculator

import com.example.nutrilog.analysis.analyzer.MealPatternAnalyzer
import com.example.nutrilog.analysis.analysis.RegularityAnalysis
import com.example.nutrilog.shared.DailyAnalysis
import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.NutritionTarget

class HealthScoreCalculatorV2(
    private val target: NutritionTarget,
    private val patternAnalyzer: MealPatternAnalyzer
) {
    fun calculateScore(dayAnalysis: DailyAnalysis): HealthScore {
        val baseScore = HealthScoreCalculatorV1(target).calculateScore(dayAnalysis.nutrition)

        // 添加饮食模式评分（占20%）
        val patternAnalysis = patternAnalyzer.analyzeMealRegularity(dayAnalysis.records)
        val patternScore = calculatePatternScore(patternAnalysis)

        val totalScore = baseScore.total * 0.8 + patternScore * 0.2

        return baseScore.copy(
            total = totalScore,
            feedback = baseScore.feedback + patternAnalysis.suggestions
        )
    }

    private fun calculatePatternScore(analysis: RegularityAnalysis): Double {
        var score = 100.0

        // 早餐规律性（40%）
        score = score * 0.6 + analysis.breakfastScore * 0.4

        // 午餐规律性（30%）
        score = score * 0.7 + analysis.lunchScore * 0.3

        // 晚餐规律性（20%）
        score = score * 0.8 + analysis.dinnerScore * 0.2

        // 夜宵频率扣分（10%）
        val lateNightPenalty = (analysis.lateNightFrequency * 100).toInt()
        score -= lateNightPenalty

        return score.coerceIn(0.0, 100.0)
    }
}