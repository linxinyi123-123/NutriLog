package com.example.nutrilog.analysis.service

import android.util.Log
import com.example.nutrilog.analysis.exception.CalculationException
import com.example.nutrilog.analysis.exception.DataNotFoundException
import com.example.nutrilog.analysis.view.AnalysisReport
import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.NutritionFacts

class SafeAnalysisService(
    private val delegate: CompleteAnalysisService
) {
    suspend fun getSafeDailyReport(date: String): Result<AnalysisReport> {
        return try {
            val report = delegate.getCompleteDailyReport(date)
            Result.success(report)
        } catch (e: DataNotFoundException) {
            // 数据不存在时返回空报告
            Result.success(createEmptyReport(date))
        } catch (e: CalculationException) {
            // 计算错误时返回部分结果
            Result.failure(e)
        } catch (e: Exception) {
            // 其他异常
            Log.e("SafeAnalysisService", "Analysis failed", e)
            Result.failure(e)
        }
    }

    private fun createEmptyReport(date: String): AnalysisReport {
        return AnalysisReport(
            date = date,
            period = "day",
            score = HealthScore(0.0, emptyMap(), listOf("暂无数据")),
            nutrition = NutritionFacts(),
            charts = emptyList(),
            insights = listOf("暂无饮食记录"),
            recommendations = listOf("开始记录你的第一餐吧！")
        )
    }
}