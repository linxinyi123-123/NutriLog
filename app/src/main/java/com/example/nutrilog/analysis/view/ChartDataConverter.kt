package com.example.nutrilog.analysis.view

import com.example.nutrilog.analysis.analysis.DailyAnalysis
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.NutritionTarget

class ChartDataConverter {
    fun convertToChartData(analysis: DailyAnalysis): List<ChartData> {
        return listOf(
            createMacroPieChart(analysis.nutrition),
            createTargetRadarChart(analysis.nutrition, analysis.target),
            createTrendChart(),
            createCategoryChart()
        )
    }
    
    private fun createMacroPieChart(nutrition: NutritionFacts): ChartData {
        val totalMacros = nutrition.protein + nutrition.carbs + nutrition.fat
        
        return ChartData(
            type = ChartType.PIE,
            title = "三大营养素比例",
            dataPoints = listOf(
                DataPoint("蛋白质", (nutrition.protein / totalMacros * 100), color = "#4CAF50"),
                DataPoint("碳水", (nutrition.carbs / totalMacros * 100), color = "#2196F3"),
                DataPoint("脂肪", (nutrition.fat / totalMacros * 100), color = "#FF9800")
            ),
            config = ChartConfig(showValues = true)
        )
    }
    
    private fun createTargetRadarChart(nutrition: NutritionFacts, target: NutritionTarget): ChartData {
        val nutrients = listOf("热量", "蛋白质", "碳水", "脂肪", "膳食纤维")
        
        val actualValues = listOf(
            nutrition.calories ?: 0.0,
            nutrition.protein ?: 0.0,
            nutrition.carbs ?: 0.0,
            nutrition.fat ?: 0.0,
            nutrition.fiber ?: 0.0
        )
        
        val targetValues = listOf(
            target.calories.max,
            target.protein.max,
            target.carbs.max,
            target.fat.max,
            target.fiber
        )
        
        // 计算达成率（0-150%）
        val achievementRates = actualValues.zip(targetValues).map { (actual, target) ->
            ((actual / target) * 100).coerceIn(0.0, 150.0)
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
    
    private fun createTrendChart(): ChartData {
        // 由于DailyAnalysis中没有trendData字段，这里创建一个模拟的趋势图
        // 实际使用时应该从分析服务获取真实的趋势数据
        val dataPoints = listOf(
            DataPoint("前1天", 80.0),
            DataPoint("前2天", 83.0),
            DataPoint("前3天", 78.0),
            DataPoint("今天", 85.0)
        )
        
        return ChartData(
            type = ChartType.LINE,
            title = "健康评分趋势",
            dataPoints = dataPoints,
            config = ChartConfig(showLegend = false)
        )
    }
    
    private fun createCategoryChart(): ChartData {
        // 由于DailyAnalysis中没有varietyData字段，这里创建一个模拟的类别分布
        // 实际使用时应该从分析服务获取真实的类别分布数据
        val mockCategoryData = mapOf(
            "谷薯类" to 30.0,
            "蔬菜类" to 25.0,
            "水果类" to 15.0,
            "蛋白质类" to 20.0,
            "奶制品" to 5.0,
            "油脂类" to 5.0
        )
        
        val dataPoints = mockCategoryData.map { (category, value) ->
            DataPoint(
                label = category,
                value = value
            )
        }
        
        return ChartData(
            type = ChartType.BAR,
            title = "食物类别分布",
            dataPoints = dataPoints,
            config = ChartConfig(showValues = true)
        )
    }
}