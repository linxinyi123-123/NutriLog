package com.example.nutrilog.analysis.analysis

import com.example.nutrilog.analysis.view.ChartData

class AnalysisCache {
    val dailyCache = mutableMapOf<String, DailyAnalysis>()
    private val weeklyCache = mutableMapOf<String, TrendAnalysis>()
    private val chartCache = mutableMapOf<String, ChartData>()

    fun getDailyAnalysis(date: String): DailyAnalysis? {
        return dailyCache[date]
    }

    fun putDailyAnalysis(date: String, analysis: DailyAnalysis) {
        dailyCache[date] = analysis
        // 限制缓存大小
        if (dailyCache.size > 30) {
            val oldestKey = dailyCache.keys.minOrNull()
            if (oldestKey != null) {
                dailyCache.remove(oldestKey)
            }
        }
    }

    fun clear() {
        dailyCache.clear()
        weeklyCache.clear()
        chartCache.clear()
    }
}