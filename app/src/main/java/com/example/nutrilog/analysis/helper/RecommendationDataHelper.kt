package com.example.nutrilog.analysis.helper

import com.example.nutrilog.analysis.analysis.NutritionalGap
import com.example.nutrilog.analysis.analysis.RecommendationAnalysis
import com.example.nutrilog.features.recommendation.interfaces.DailyScore
import com.example.nutrilog.features.recommendation.interfaces.EatingPatternAnalysis
import com.example.nutrilog.data.repository.MealRecordRepository
import java.time.LocalDate

data class WeeklySummary(
    val averageScore: Double,
    val mainGaps: List<NutritionalGap>,
    val dominantPatterns: List<String>,
    val improvementAreas: List<String>,
    val startDate: String,
    val endDate: String
)

class RecommendationDataHelper(
    private val analysisService: RecommendationAnalysis,
    private val recordRepository: MealRecordRepository
) {
    // 获取用户最近一周的饮食概况
    suspend fun getWeeklySummary(userId: Long): WeeklySummary {
        val endDate = LocalDate.now().toString()
        val startDate = LocalDate.now().minusDays(7).toString()
        
        val gaps = analysisService.identifyNutritionalGaps(userId, 7)
        val patterns = analysisService.analyzeEatingPatterns(userId, startDate, endDate)
        val scores = analysisService.getHealthScoreHistory(userId, 7)

        return WeeklySummary(
            averageScore = scores.map { it.score.toDouble() }.average(),
            mainGaps = gaps.take(3),
            dominantPatterns = patterns.unhealthyPatterns.take(2),
            improvementAreas = identifyImprovementAreas(gaps, patterns),
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun identifyImprovementAreas(
        gaps: List<NutritionalGap>,
        patterns: EatingPatternAnalysis
    ): List<String> {
        val areas = mutableListOf<String>()

        // 添加营养缺口相关的改进建议
        gaps.forEach { gap ->
            when (gap.nutrient) {
                "protein" -> areas.add("增加蛋白质摄入，建议多吃鱼类、瘦肉、豆制品等")
                "fiber" -> areas.add("增加膳食纤维摄入，建议多吃蔬菜、水果、全谷物")
                "calories" -> areas.add("调整热量摄入，${if (gap.averageIntake < gap.recommended) "适当增加" else "适当减少"}每日热量")
                "carbs" -> areas.add("调整碳水化合物摄入，选择优质碳水如全谷物")
                "fat" -> areas.add("调整脂肪摄入，选择健康脂肪如坚果、橄榄油")
            }
        }

        // 添加饮食模式相关的改进建议
        if (patterns.mealRegularity < 60) {
            areas.add("改善饮食规律性，尽量在固定时间用餐")
        }

        if (patterns.foodVariety < 20) {
            areas.add("增加食物种类多样性，尝试不同类型的食物")
        }

        patterns.unhealthyPatterns.forEach { pattern ->
            when (pattern) {
                "late_night_eating" -> areas.add("避免深夜进食，晚餐尽量在睡前3小时完成")
                "skipping_breakfast" -> areas.add("养成吃早餐的习惯，保证上午能量供应")
                "excessive_snacking" -> areas.add("控制零食摄入，选择健康的零食替代")
                "irregular_meals" -> areas.add("保持规律的用餐时间")
            }
        }

        return areas.take(5)
    }
}