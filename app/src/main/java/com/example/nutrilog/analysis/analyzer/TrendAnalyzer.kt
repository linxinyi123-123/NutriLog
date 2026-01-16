package com.example.nutrilog.analysis.analyzer

import com.example.nutrilog.analysis.analysis.DailyTrendPoint
import com.example.nutrilog.analysis.analysis.TrendAnalysis
import com.example.nutrilog.analysis.analysis.TrendDirection
import com.example.nutrilog.analysis.calculator.BasicNutritionCalculator
import com.example.nutrilog.shared.MealRecord
import com.example.nutrilog.shared.NutritionFacts

class TrendAnalyzer {
    // 分析周趋势
    fun analyzeWeeklyTrend(records: List<MealRecord>): TrendAnalysis {
        val dailyAnalyses = mutableListOf<DailyTrendPoint>()

        // 按日期分组
        val recordsByDate = records.groupBy { it.date }

        recordsByDate.forEach { (date, dayRecords) ->
            // 计算每日营养
            val nutrition = BasicNutritionCalculator()
                .calculateDailyNutrition(dayRecords)

            // 计算每日健康评分（简化版）
            val score = calculateDailyHealthScore(nutrition)

            dailyAnalyses.add(DailyTrendPoint(date, nutrition, score))
        }

        // 计算趋势指标
        val calorieTrend = calculateTrend(dailyAnalyses.map { it.nutrition.calories })
        val proteinTrend = calculateTrend(dailyAnalyses.map { it.nutrition.protein })
        val scoreTrend = calculateTrend(dailyAnalyses.map { it.score })

        return TrendAnalysis(
            period = "week",
            startDate = dailyAnalyses.minByOrNull { it.date }?.date ?: "",
            endDate = dailyAnalyses.maxByOrNull { it.date }?.date ?: "",
            dailyPoints = dailyAnalyses.sortedBy { it.date },
            trends = mapOf(
                "calories" to calorieTrend,
                "protein" to proteinTrend,
                "score" to scoreTrend
            ),
            insights = generateTrendInsights(dailyAnalyses)
        )
    }

    private fun calculateTrend(values: List<Double>): TrendDirection {
        if (values.size < 2) return TrendDirection.STABLE

        // 简单线性趋势判断
        val firstHalf = values.take(values.size / 2).average()
        val secondHalf = values.takeLast(values.size / 2).average()

        return when {
            secondHalf > firstHalf * 1.1 -> TrendDirection.UP
            secondHalf < firstHalf * 0.9 -> TrendDirection.DOWN
            else -> TrendDirection.STABLE
        }
    }

    /**
     * 计算每日健康评分 (0-100分)
     * 基于卡路里、蛋白质、脂肪、碳水化合物等营养素的平衡性
     */
    public fun calculateDailyHealthScore(nutrition: NutritionFacts): Double {
        var score = 100.0

        // 假设的营养目标值（可根据实际情况调整）
        val targetCalories = 2000.0  // 目标卡路里
        val targetProteinRatio = 0.15 // 蛋白质占总卡路里的比例
        val targetFatRatio = 0.25     // 脂肪占总卡路里的比例
        val targetCarbRatio = 0.60    // 碳水占总卡路里的比例

        // 卡路里评分 (±20%为满分范围)
        val calorieRatio = nutrition.calories  / targetCalories
        score -= when {
            calorieRatio in 0.8..1.2 -> 0.0
            calorieRatio in 0.6..0.8 || calorieRatio in 1.2..1.4 -> 10.0
            calorieRatio in 0.4..0.6 || calorieRatio in 1.4..1.6 -> 20.0
            else -> 30.0
        }

        // 计算三大营养素提供的卡路里
        val proteinCalories = nutrition.protein * 4  // 1g蛋白质=4卡路里
        val fatCalories = nutrition.fat * 9          // 1g脂肪=9卡路里
        val carbCalories = nutrition.carbs * 4       // 1g碳水=4卡路里
        val totalCaloriesFromMacros = proteinCalories + fatCalories + carbCalories

        if (totalCaloriesFromMacros > 0) {
            // 蛋白质比例评分
            val actualProteinRatio = proteinCalories / totalCaloriesFromMacros
            score -= when {
                actualProteinRatio in (targetProteinRatio - 0.03)..(targetProteinRatio + 0.03) -> 0.0
                actualProteinRatio in (targetProteinRatio - 0.06)..(targetProteinRatio + 0.06) -> 5.0
                else -> 15.0
            }

            // 脂肪比例评分
            val actualFatRatio = fatCalories / totalCaloriesFromMacros
            score -= when {
                actualFatRatio in (targetFatRatio - 0.05)..(targetFatRatio + 0.05) -> 0.0
                actualFatRatio in (targetFatRatio - 0.10)..(targetFatRatio + 0.10) -> 5.0
                else -> 15.0
            }

            // 碳水比例评分
            val actualCarbRatio = carbCalories / totalCaloriesFromMacros
            score -= when {
                actualCarbRatio in (targetCarbRatio - 0.08)..(targetCarbRatio + 0.08) -> 0.0
                actualCarbRatio in (targetCarbRatio - 0.15)..(targetCarbRatio + 0.15) -> 5.0
                else -> 15.0
            }
        }

        // 微量营养素奖励（如果有相关数据）
        // 这里可以添加维生素、矿物质等的评分逻辑

        return score.coerceIn(0.0, 100.0)  // 确保分数在0-100之间
    }

    /**
     * 生成趋势洞察和建议
     */
    private fun generateTrendInsights(dailyAnalyses: List<DailyTrendPoint>): List<String> {
        val insights = mutableListOf<String>()

        if (dailyAnalyses.size < 3) {
            insights.add("数据天数较少，建议收集更多数据以获得更准确的趋势分析")
            return insights
        }

        // 分析卡路里趋势
        val calorieValues = dailyAnalyses.map { it.nutrition.calories }
        val calorieTrend = calculateTrend(calorieValues)
        when (calorieTrend) {
            TrendDirection.UP -> insights.add("📈 卡路里摄入呈上升趋势，注意控制总热量避免超标")
            TrendDirection.DOWN -> insights.add("📉 卡路里摄入呈下降趋势，确保营养充足避免过低")
            TrendDirection.STABLE -> insights.add("✅ 卡路里摄入保持稳定，有利于体重管理")
        }

        // 分析蛋白质趋势
        val proteinValues = dailyAnalyses.map { it.nutrition.protein }
        val proteinTrend = calculateTrend(proteinValues)
        when (proteinTrend) {
            TrendDirection.UP -> insights.add("💪 蛋白质摄入增加，有助于肌肉维持和修复")
            TrendDirection.DOWN -> insights.add("⚠️ 蛋白质摄入不足，建议增加优质蛋白食物")
            TrendDirection.STABLE -> insights.add("🥩 蛋白质摄入稳定，符合均衡饮食要求")
        }

        // 分析健康评分趋势
        val scoreValues = dailyAnalyses.map { it.score }
        val scoreTrend = calculateTrend(scoreValues)
        when (scoreTrend) {
            TrendDirection.UP -> insights.add("🌟 整体健康评分提升，饮食习惯持续改善")
            TrendDirection.DOWN -> insights.add("📉 健康评分下降，建议关注营养均衡搭配")
            TrendDirection.STABLE -> insights.add("😊 健康评分稳定，继续保持良好饮食习惯")
        }

        // 分析具体数值范围
        val avgCalories = calorieValues.average()
        when {
            avgCalories > 2500 -> insights.add("🔥 平均卡路里较高，建议选择低热量高营养密度食物")
            avgCalories < 1500 -> insights.add("⚡ 平均卡路里偏低，可能影响身体机能，建议适当增加")
        }

        val avgProtein = proteinValues.average()
        val calorieForProteinRatio = if (avgCalories > 0) (avgProtein * 4 / avgCalories) else 0.0
        when {
            calorieForProteinRatio > 0.25 -> insights.add("🐔 蛋白质比例偏高，注意搭配其他营养素")
            calorieForProteinRatio < 0.10 -> insights.add("🥗 蛋白质比例偏低，建议增加瘦肉、蛋奶、豆类摄入")
        }

        // 添加个性化建议
        val latestScore = dailyAnalyses.last().score
        when {
            latestScore >= 90 -> insights.add("🏆 健康状况优秀！继续保持科学的饮食搭配")
            latestScore >= 70 -> insights.add("👍 健康状况良好，可进一步优化营养结构")
            latestScore >= 50 -> insights.add("💡 健康状况一般，建议制定更均衡的饮食计划")
            else -> insights.add("🚨 健康状况需要关注，建议咨询营养师制定改善方案")
        }

        return insights.distinct() // 去重后返回
    }
}