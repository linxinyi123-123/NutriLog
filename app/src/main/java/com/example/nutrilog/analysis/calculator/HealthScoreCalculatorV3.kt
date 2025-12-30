package com.example.nutrilog.analysis.calculator

import com.example.nutrilog.analysis.analyzer.FoodVarietyAnalyzer
import com.example.nutrilog.analysis.analyzer.MealPatternAnalyzer
import com.example.nutrilog.shared.DailyAnalysis
import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.MealRecord
import com.example.nutrilog.shared.NutritionTarget

class HealthScoreCalculatorV3(
    private val target: NutritionTarget,
    private val patternAnalyzer: MealPatternAnalyzer,
    private val varietyAnalyzer: FoodVarietyAnalyzer
) {
    fun calculateScore(weekRecords: List<MealRecord>, dayAnalysis: DailyAnalysis): HealthScore {
        val v2Score = HealthScoreCalculatorV2(target, patternAnalyzer)
            .calculateScore(dayAnalysis)

        // 添加食物多样性评分（占15%）
        val varietyAnalysis = varietyAnalyzer.analyzeVariety(weekRecords)
        val varietyScore = varietyAnalysis.totalScore

        val totalScore = v2Score.total * 0.85 + varietyScore * 0.15

        return v2Score.copy(
            total = totalScore,
            feedback = v2Score.feedback + varietyAnalysis.suggestions
        )
    }
}