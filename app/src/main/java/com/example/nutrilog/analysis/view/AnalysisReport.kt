package com.example.nutrilog.analysis.view

import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.NutritionFacts

data class AnalysisReport(
    val date: String,
    val period: String,  // day/week/month
    val score: HealthScore,
    val nutrition: NutritionFacts,
    val charts: List<ChartData>,
    val insights: List<String>,
    val recommendations: List<String>,
    val comparison: ComparisonData? = null
)

data class ComparisonData(
    val comparedTo: String,  // target/history/average
    val differences: Map<String, Double>  // 营养素差异
)