package com.example.nutrilog.analysis.generator

import com.example.nutrilog.analysis.analysis.DailyTrendPoint

class InsightGenerator {
    fun generateTrendInsights(points: List<DailyTrendPoint>): List<String> {
        val insights = mutableListOf<String>()

        if (points.size < 3) return insights

        // 1. 热量趋势洞察
        val calories = points.map { it.nutrition.calories }
        val avgCalories = calories.average()
        val calorieVariation = calculateVariation(calories)

        if (calorieVariation > 30) {
            insights.add("本周热量摄入波动较大，建议保持稳定")
        }

        // 2. 蛋白质摄入洞察
        val proteins = points.map { it.nutrition.protein }
        val avgProtein = proteins.average()

        if (avgProtein < 60) {
            insights.add("本周蛋白质摄入不足，建议增加蛋白质食物")
        }

        // 3. 健康评分趋势洞察
        val scores = points.map { it.score }
        val scoreTrend = calculateLinearTrend(scores)

        if (scoreTrend > 0.5) {
            insights.add("本周饮食质量持续改善，继续保持！")
        } else if (scoreTrend < -0.5) {
            insights.add("本周饮食质量有所下降，需要关注")
        }

        return insights
    }

    /**
     * 计算变异系数 (Coefficient of Variation)
     * 用于衡量数据的相对离散程度，公式为: (标准差 / 平均值) × 100%
     * 返回值以百分比表示，便于理解
     */
    private fun calculateVariation(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0

        val average = values.average()
        if (average == 0.0) return 0.0

        // 计算标准差
        val variance = values.map { (it - average) * (it - average) }.average()
        val standardDeviation = Math.sqrt(variance)

        // 计算变异系数 (以百分比表示)
        return (standardDeviation / average) * 100
    }

    /**
     * 计算线性趋势斜率
     * 使用最小二乘法计算线性回归的斜率
     * 正值表示上升趋势，负值表示下降趋势，绝对值大小表示趋势强度
     */
    private fun calculateLinearTrend(values: List<Double>): Double {
        if (values.size < 2) return 0.0

        val n = values.size
        val xValues = List(n) { it.toDouble() } // 时间点: 0, 1, 2, ..., n-1

        // 计算必要的求和值
        val sumX = xValues.sum()
        val sumY = values.sum()
        val sumXY = xValues.zip(values) { x, y -> x * y }.sum()
        val sumX2 = xValues.map { it * it }.sum()

        // 使用最小二乘法计算斜率
        // 公式: slope = (n*Σ(xy) - Σx*Σy) / (n*Σ(x²) - (Σx)²)
        val numerator = n * sumXY - sumX * sumY
        val denominator = n * sumX2 - sumX * sumX

        return if (denominator != 0.0) numerator / denominator else 0.0
    }

}