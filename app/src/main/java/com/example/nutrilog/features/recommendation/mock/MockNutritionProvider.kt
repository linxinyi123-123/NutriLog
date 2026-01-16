package com.example.nutrilog.features.recommendation.mock

import com.example.nutrilog.features.recommendation.interfaces.EatingPatternAnalysis
import com.example.nutrilog.features.recommendation.interfaces.NutritionProvider
import com.example.nutrilog.features.recommendation.interfaces.NutritionalGap
import com.example.nutrilog.features.recommendation.interfaces.Severity

// features/recommendation/mock/MockNutritionProvider.kt
class MockNutritionProvider : NutritionProvider {
    override suspend fun getNutritionalGaps(userId: Long, days: Int): List<NutritionalGap> {
        // 精心设计的模拟数据，展示系统能力
        return listOf(
            NutritionalGap(
                nutrient = "protein",
                averageIntake = 45.0,  // 偏低
                recommended = 70.0,
                gapPercentage = 35.7,
                severity = Severity.SEVERE
            ),
            NutritionalGap(
                nutrient = "fiber",
                averageIntake = 18.0,  // 偏低
                recommended = 25.0,
                gapPercentage = 28.0,
                severity = Severity.MODERATE
            ),
            NutritionalGap(
                nutrient = "vitamin_c",
                averageIntake = 65.0,  // 足够
                recommended = 60.0,
                gapPercentage = -8.3,  // 负值表示超过推荐
                severity = Severity.MILD
            )
        )
    }

    override suspend fun getEatingPatterns(userId: Long): EatingPatternAnalysis {
        return EatingPatternAnalysis(
            mealRegularity = 78.5,  // 规律性评分
            timeDistribution = mapOf(
                "早餐" to 0.25,     // 早餐占比25%
                "午餐" to 0.35,     // 午餐占比35%
                "晚餐" to 0.30,     // 晚餐占比30%
                "宵夜" to 0.10      // 宵夜占比10%
            ),
            foodVariety = 15,  // 食物种类数量
            unhealthyPatterns = listOf(
                "跳过早餐",
                "晚餐时间不规律",
                "高糖零食摄入过多"
            )
        )
    }

    override suspend fun getLatestHealthScore(userId: Long): Int {
        return 72  // 中等健康分数
    }
}