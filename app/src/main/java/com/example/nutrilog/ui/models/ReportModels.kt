package com.example.nutrilog.ui.models

import java.time.LocalDate
import java.time.LocalDateTime

// 报告类型枚举
enum class ReportType {
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}

// 报告数据模型
data class GeneratedReport(
    val id: String,
    val type: ReportType,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val contentOptions: ContentOptions,
    val styleOptions: StyleOptions,
    val averageScore: Double = 85.5,
    val generatedTime: LocalDateTime = LocalDateTime.now(),
    val generationDate: LocalDate = LocalDate.now()
)

// 分享格式枚举
enum class ShareFormat {
    PDF,
    IMAGE,
    TEXT;
    
    val displayName: String
        get() = when (this) {
            PDF -> "PDF文档"
            IMAGE -> "图片"
            TEXT -> "文本摘要"
        }
    
    val description: String
        get() = when (this) {
            PDF -> "完整的PDF格式报告"
            IMAGE -> "报告封面图片"
            TEXT -> "报告文本摘要"
        }
}

// 报告内容选项
data class ContentOptions(
    val includeNutritionSummary: Boolean = true,
    val includeTrendAnalysis: Boolean = true,
    val includeMealPatterns: Boolean = true,
    val includeRecommendations: Boolean = true,
    val includeHealthScore: Boolean = true
)

// 报告样式选项
data class StyleOptions(
    val colorScheme: ColorScheme = ColorScheme.DEFAULT,
    val chartType: ChartType = ChartType.BAR,
    val includeCharts: Boolean = true,
    val includeTables: Boolean = true
)

// 颜色方案枚举
enum class ColorScheme {
    DEFAULT,
    VIBRANT,
    MINIMAL,
    MONOCHROME
}

// 图表类型枚举
enum class ChartType {
    BAR,
    LINE,
    PIE,
    RADAR
}
