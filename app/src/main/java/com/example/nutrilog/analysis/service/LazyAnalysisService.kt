package com.example.nutrilog.analysis.service

import com.example.nutrilog.analysis.analysis.AnalysisCache
import com.example.nutrilog.analysis.analyzer.FoodVarietyAnalyzer
import com.example.nutrilog.analysis.analyzer.MealPatternAnalyzer
import com.example.nutrilog.analysis.calculator.BasicNutritionCalculator
import com.example.nutrilog.analysis.calculator.HealthScoreCalculatorV3
import com.example.nutrilog.analysis.calculator.PersonalizedTargetCalculator
import com.example.nutrilog.analysis.repository.AnalysisRepository
import com.example.nutrilog.shared.DailyAnalysis
import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.MealRecord
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.NutritionTargetFactory

class LazyAnalysisService(
    private val repository: AnalysisRepository,
    private val cache: AnalysisCache,
    private val nutritionCalculator: BasicNutritionCalculator,
    private val scoreCalculator: HealthScoreCalculatorV3,
    private val patternAnalyzer: MealPatternAnalyzer,
    private val varietyAnalyzer: FoodVarietyAnalyzer
) {
    // 懒加载日分析
    suspend fun getDailyAnalysis(date: String): DailyAnalysis {
        return (cache.getDailyAnalysis(date) ?: computeAndCache(date)) as DailyAnalysis
    }

    private suspend fun computeAndCache(date: String): DailyAnalysis {
        // 计算分析
        val analysis = computeAnalysis(date)
        // 缓存结果
        cache.putDailyAnalysis(date, analysis)
        return analysis
    }

    // 增量计算（当新记录添加时）
    suspend fun updateAnalysisWithNewRecord(record: MealRecord) {
        val date = record.date

        // 获取现有分析
        val existingAnalysis = cache.getDailyAnalysis(date)
        if (existingAnalysis != null) {
            // 增量更新
            val updatedAnalysis = incrementallyUpdate(existingAnalysis, record)
            cache.putDailyAnalysis(date, updatedAnalysis)
        } else {
            // 重新计算
            cache.dailyCache.remove(date) // 清除缓存，下次重新计算
        }

        // 清除周缓存（因为周数据已变）
        clearWeeklyCacheForDate(date)
    }

    private suspend fun computeAnalysis(date: String): DailyAnalysis {
        // 1. 获取记录
        val records = repository.getRecordsByDate(date)

        // 2. 计算营养
        val nutrition = nutritionCalculator.calculateDailyNutrition(records)

        // 3. 获取用户目标
        val user = repository.getUserProfile()
        val target = if (user != null) {
            PersonalizedTargetCalculator().calculateTargets(user)
        } else {
            NutritionTargetFactory().createForAdultMale()
        }

        val fact = NutritionFacts(
            calories = target.calories.min,
            protein = target.protein.min,
            carbs = target.carbs.min,
            fat = target.fat.min,
            fiber = target.fiber,
            sugar = target.sugar,
            sodium = target.sodium
        )

        // 4. 计算健康评分
        val score = scoreCalculator.calculateScore(records,
            DailyAnalysis(date, HealthScore(0.0, emptyMap(), emptyList()),nutrition, fact,records)
        )

        return DailyAnalysis(
            date = date,
            nutrition = nutrition,
            target = fact,
            score = score,
            records = records
        )
    }

    private fun incrementallyUpdate(
        existingAnalysis: DailyAnalysis,  // 将参数类型改为非空
        newRecord: MealRecord
    ): DailyAnalysis {
        // 1. 计算新记录的营养
        val newRecordNutrition = nutritionCalculator.calculateMealNutrition(newRecord.foods)

        // 2. 更新总营养
        val updatedNutrition = existingAnalysis.nutrition.copy().plus(newRecordNutrition)

        // 3. 更新记录列表
        val updatedRecords = existingAnalysis.records + newRecord

        // 4. 重新计算健康评分（由于营养数据变化）
        val updatedScore = scoreCalculator.calculateScore(
            updatedRecords,
            DailyAnalysis(
                date = existingAnalysis.date,
                nutrition = updatedNutrition,
                target = existingAnalysis.target,
                score = HealthScore(0.0, emptyMap(), emptyList()),
                records = updatedRecords
            )
        )

        return existingAnalysis.copy(
            nutrition = updatedNutrition,
            score = updatedScore,
            records = updatedRecords
        )
    }

    private fun clearWeeklyCacheForDate(date: String) {
        // 解析日期，确定该日期属于哪一周
        val yearWeek = getYearWeekFromDate(date)

        // 构建周缓存键（假设使用 "week_YYYY_WW" 格式）
        val weekKey = "week_$yearWeek"

        // 清除对应的周缓存
        cache.weeklyCache.remove(weekKey)

        // 同时清除可能受影响的月缓存
        val monthKey = "month_${date.substring(0, 7)}" // YYYY-MM
        cache.weeklyCache.remove(monthKey)
    }

    private fun getYearWeekFromDate(dateString: String): String {
        // 简单的实现：将日期转换为 "YYYY_WW" 格式
        // 实际项目中建议使用更健壮的日期处理库
        try {
            val parts = dateString.split("-")
            val year = parts[0]
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            // 简化的周计算（ISO 8601标准的简化版）
            val calendar = java.util.Calendar.getInstance()
            calendar.set(year.toInt(), month - 1, day)
            val week = (calendar.get(java.util.Calendar.WEEK_OF_YEAR) + 52 *
                    (calendar.get(java.util.Calendar.YEAR) - 1970)) % 52
            val weekFormatted = (week + 1).toString().padStart(2, '0')

            return "${year}_$weekFormatted"
        } catch (e: Exception) {
            return "unknown_week"
        }
    }

    // 批量更新多个记录
    suspend fun updateAnalysisWithMultipleRecords(records: List<MealRecord>) {
        val recordsByDate = records.groupBy { it.date }

        recordsByDate.forEach { (date, dateRecords) ->
            val existingAnalysis = cache.getDailyAnalysis(date)
            if (existingAnalysis != null) {
                // 批量增量更新
                var updatedAnalysis = existingAnalysis
                dateRecords.forEach { record ->
                    updatedAnalysis = incrementallyUpdate(existingAnalysis, record)
                }
                cache.putDailyAnalysis(date, updatedAnalysis)
            } else {
                // 重新计算
                cache.dailyCache.remove(date)
            }
        }

        // 清除所有受影响的周缓存
        recordsByDate.keys.forEach { date ->
            clearWeeklyCacheForDate(date)
        }
    }
}