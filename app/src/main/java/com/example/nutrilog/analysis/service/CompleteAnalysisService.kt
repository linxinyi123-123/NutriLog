package com.example.nutrilog.analysis.service

import com.example.nutrilog.analysis.analysis.AnalysisCache
import com.example.nutrilog.analysis.analysis.DailyAnalysis
import com.example.nutrilog.analysis.analysis.DailyTrendPoint
import com.example.nutrilog.analysis.analyzer.FoodVarietyAnalyzer
import com.example.nutrilog.analysis.analyzer.MealPatternAnalyzer
import com.example.nutrilog.analysis.calculator.BasicNutritionCalculator
import com.example.nutrilog.analysis.calculator.HealthScoreCalculatorV3
import com.example.nutrilog.analysis.calculator.PersonalizedTargetCalculator
import com.example.nutrilog.analysis.exception.CalculationException
import com.example.nutrilog.analysis.exception.DataNotFoundException
import com.example.nutrilog.analysis.generator.InsightGenerator
import com.example.nutrilog.analysis.view.AnalysisReport
import com.example.nutrilog.analysis.view.ChartDataFactory
import com.example.nutrilog.common.models.UserProfile
import com.example.nutrilog.data.dao.RecordFoodDao
import com.example.nutrilog.data.entities.FoodItemWithAmount
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.repository.MealRecordRepository
import com.example.nutrilog.features.recommendation.repository.UserRepository
import com.example.nutrilog.shared.FoodItem
import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.NutritionFacts
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class CompleteAnalysisService(
    private val recordRepository: MealRecordRepository, // 同学A提供
    private val userProfile:UserProfile,
    private val cache: AnalysisCache,
    private val calculator : BasicNutritionCalculator,
    private val recordFoodDao: RecordFoodDao,
    private val foodItem : com.example.nutrilog.data.entities.FoodItem,
    private val insighGenerator: InsightGenerator
) {

    private val targetCalculator = PersonalizedTargetCalculator()
    private val patternAnalyzer = MealPatternAnalyzer()
    private val varietyAnalyzer = FoodVarietyAnalyzer()
    private val chartFactory = ChartDataFactory()

    suspend fun getCompleteDailyReport(date: String): AnalysisReport {
        // 1. 获取数据
        val records = recordRepository.getMealRecordsByDate(date)

        if (records.isEmpty()) {
            throw DataNotFoundException("No meal records found for date: $date")
        }

        // 2. 计算分析
        val nutrition = try {
            calculator.calculateDailyNutrition(convertWithEstimation(records))
        } catch (e: Exception) {
            throw CalculationException("Failed to calculate nutrition for date: $date", e)
        }

        val target = try {
            targetCalculator.calculateTargets(userProfile)
        } catch (e: Exception) {
            throw CalculationException("Failed to calculate nutrition targets", e)
        }

        // 获取一周数据用于多样性分析
        val weekStart = getWeekStartDate(date)
        val weekRecords = try {
            convertWithEstimation(recordRepository.getMealRecordsByDateRange(weekStart,date))
        } catch (e: Exception) {
            throw CalculationException("Failed to get weekly records", e)
        }

        val item = NutritionFacts(
            calories = target.calories.min,
            protein = target.protein.min,
            carbs = target.carbs.min,
            fat = target.fat.min,
            sodium = target.sodium,                // 5克盐约等于2000mg钠
            fiber = target.fiber,                    // 每日25克膳食纤维
            sugar = target.sugar                   // 每日不超过50克添加糖
        )
        // 3. 计算评分
        val scoreCalculator = HealthScoreCalculatorV3(target, patternAnalyzer, varietyAnalyzer)
        val score = try {
            scoreCalculator.calculateScore(weekRecords,
                com.example.nutrilog.shared.DailyAnalysis(
                    date, HealthScore(0.0,mapOf<String, Double>(),emptyList()),nutrition,
                    item,convertWithEstimation(records)))
        } catch (e: Exception) {
            throw CalculationException("Failed to calculate health score", e)
        }

        // 4. 生成图表
        val charts = try {
            listOf(
                chartFactory.createMacroPieChart(nutrition),
                chartFactory.createTargetRadarChart(nutrition, target)
            )
        } catch (e: Exception) {
            throw CalculationException("Failed to generate charts", e)
        }

        val dailyTrendPoint = DailyTrendPoint(
            date,nutrition,score.total
        )

        val listPoint = mutableListOf<DailyTrendPoint>()
        listPoint.add(dailyTrendPoint)

        // 5. 生成洞察
        val insights = try {
            insighGenerator.generateTrendInsights(listPoint)
        } catch (e: Exception) {
            throw CalculationException("Failed to generate insights", e)
        }

        return AnalysisReport(
            date = date,
            period = "day",
            score = score,
            nutrition = nutrition,
            charts = charts,
            insights = insights,
            recommendations = score.feedback
        )
    }

    suspend fun convertWithEstimation(oldRecords: List<MealRecord>): List<com.example.nutrilog.shared.MealRecord> {
        return oldRecords.map { old ->
            com.example.nutrilog.shared.MealRecord(
                id = old.id,
                date = old.date,
                time = old.time,
                mealType = old.mealType,
                location = old.location,
                mood = old.mood,
                foods = getFood(recordFoodDao.getFoodsForRecord(old.id))
            )
        }
    }

    fun getFood(list: List<FoodItemWithAmount>): List<Pair<FoodItem, Double>> {
        return list.map { food ->
            foodItem.toSharedFoodItem(food.food) to food.amount

        }
    }

    fun getWeekStartDate(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = LocalDate.parse(date, formatter)

        // 获取本周一的日期（周一到周日为一周）
        val monday = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        return monday.format(formatter)
    }

    suspend fun notifyAnalysisUpdated(date: String) {
        getCompleteDailyReport(date)
    }
}