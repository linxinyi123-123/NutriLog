package com.example.nutrilog.analysis.view

import com.example.nutrilog.analysis.analysis.DailyTrendPoint
import com.example.nutrilog.analysis.analysis.VarietyAnalysis
import com.example.nutrilog.shared.FoodCategory
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.NutritionTarget

class ChartDataFactory {
    // 创建营养比例饼图数据
    fun createMacroPieChart(nutrition: NutritionFacts): ChartData {
        val totalMacros = nutrition.protein + nutrition.carbs + nutrition.fat

        val dataPoints = listOf(
            DataPoint(
                label = "蛋白质",
                value = nutrition.protein / totalMacros * 100,
                color = "#4CAF50"  // 绿色
            ),
            DataPoint(
                label = "碳水",
                value = nutrition.carbs / totalMacros * 100,
                color = "#2196F3"  // 蓝色
            ),
            DataPoint(
                label = "脂肪",
                value = nutrition.fat / totalMacros * 100,
                color = "#FF9800"  // 橙色
            )
        )

        return ChartData(
            type = ChartType.PIE,
            title = "三大营养素比例",
            dataPoints = dataPoints,
            config = ChartConfig(showValues = true)
        )
    }

    // 创建每日趋势折线图数据
    fun createDailyTrendChart(dailyPoints: List<DailyTrendPoint>): ChartData {
        val dataPoints = dailyPoints.map { point ->
            DataPoint(
                label = point.date.substring(5),  // 去掉年份
                value = point.score,
                extra = mapOf("calories" to point.nutrition.calories)
            )
        }

        return ChartData(
            type = ChartType.LINE,
            title = "健康评分趋势",
            dataPoints = dataPoints,
            config = ChartConfig(showLegend = false)
        )
    }

    // 创建食物类别分布柱状图
    fun createCategoryBarChart(varietyAnalysis: VarietyAnalysis): ChartData {
        val dataPoints = varietyAnalysis.coverage.entries.map { (category, coverage) ->
            DataPoint(
                label = category.chineseName,
                value = coverage,
                color = getColorForCategory(category)
            )
        }

        return ChartData(
            type = ChartType.BAR,
            title = "食物类别覆盖率",
            dataPoints = dataPoints,
            config = ChartConfig(showValues = true)
        )
    }

    // 创建与目标对比雷达图
    fun createTargetRadarChart(actual: NutritionFacts, target: NutritionTarget): ChartData {
        val nutrients = listOf("热量", "蛋白质", "碳水", "脂肪", "膳食纤维")

        val actualValues = listOf(
            actual.calories,
            actual.protein,
            actual.carbs,
            actual.fat,
            actual.fiber
        )

        val targetValues = listOf(
            target.calories.max,
            target.protein.max,
            target.carbs.max,
            target.fat.max,
            target.fiber
        )

        // 计算达成率（0-100%）
        val achievementRates = actualValues.zip(targetValues).map { (actual, target) ->
            ((actual?.div(target) ?: (0.0 * 100))).coerceIn(0.0, 150.0)
        }

        val dataPoints = nutrients.zip(achievementRates).map { (nutrient, rate) ->
            DataPoint(
                label = nutrient,
                value = rate,
                extra = mapOf("target" to 100.0)
            )
        }

        return ChartData(
            type = ChartType.RADAR,
            title = "营养素达成率",
            dataPoints = dataPoints,
            config = ChartConfig(showLegend = true)
        )
    }

    /**
     * 根据食物类别获取对应的颜色
     * 使用语义化颜色映射，让不同类别有独特的视觉标识
     */
    private fun getColorForCategory(category: com.example.nutrilog.shared.FoodCategory): String {
        return when (category) {
            FoodCategory.GRAINS -> "#8BC34A"
            FoodCategory.VEGETABLES -> "#4CAF50"
            FoodCategory.FRUITS -> "#FF9800"
            FoodCategory.PROTEIN -> "#F44336"
            FoodCategory.DAIRY -> "#2196F3"
            FoodCategory.NUTS-> "#795548"
            FoodCategory.OILS -> "#FFC107"
            FoodCategory.SNACKS -> "#8D6E63"
            FoodCategory.BEVERAGES -> "#03A9F4"
            FoodCategory.SEASONINGS -> "#607D8B"
            FoodCategory.OTHERS -> "#9C27B0"

        }
    }
}