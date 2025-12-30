package com.example.nutrilog.analysis.view

// 图表数据类
data class ChartData(
    val type: ChartType,
    val title: String,
    val dataPoints: List<DataPoint>,
    val config: ChartConfig
)

data class DataPoint(
    val label: String,
    val value: Double,
    val color: String? = null,
    val extra: Map<String, Any> = emptyMap()
)

enum class ChartType {
    PIE,        // 饼图/环形图
    BAR,        // 柱状图
    LINE,       // 折线图
    RADAR       // 雷达图
}

data class ChartConfig(
    val showLegend: Boolean = true,
    val showValues: Boolean = true,
    val animation: Boolean = true
)