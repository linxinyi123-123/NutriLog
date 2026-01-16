package com.example.nutrilog.analysis.analyzer

import com.example.nutrilog.analysis.analysis.NutritionalGap
import com.example.nutrilog.analysis.analysis.RecommendationAnalysis
import com.example.nutrilog.analysis.analysis.Severity
import com.example.nutrilog.analysis.calculator.BasicNutritionCalculator
import com.example.nutrilog.analysis.calculator.PersonalizedTargetCalculator
import com.example.nutrilog.common.models.UserProfile
import com.example.nutrilog.data.dao.RecordFoodDao
import com.example.nutrilog.data.entities.FoodItemWithAmount
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.repository.MealRecordRepository
import com.example.nutrilog.features.recommendation.interfaces.DailyScore
import com.example.nutrilog.features.recommendation.interfaces.EatingPatternAnalysis
import com.example.nutrilog.features.recommendation.repository.UserRepository
import com.example.nutrilog.shared.FoodItem
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.NutritionTarget
import java.time.LocalDate
import kotlin.time.Duration.Companion.days

class NutritionalGapAnalyzer(
    private val recordRepository: MealRecordRepository,
    private val targetCalculator: PersonalizedTargetCalculator,
    val basicNutritionCalculator:BasicNutritionCalculator,
    private val recordFoodDao: RecordFoodDao,
    private val foodItem : com.example.nutrilog.data.entities.FoodItem,
    private val trendAnalyzer: TrendAnalyzer,
    private val user: UserProfile
) : RecommendationAnalysis {

    suspend fun identifyNutritionalGaps(): List<NutritionalGap> {
        // 获取历史记录
        val endDate = LocalDate.now().toString()
        val startDate = LocalDate.now().minusDays(7L).toString()
        val records = convertWithEstimation(recordRepository.getMealRecordsByDateRange(startDate, endDate))


        // 计算平均摄入
        val dailyNutritions = calculateDailyNutritions(records)
        val averages = calculateAverages(dailyNutritions)

        // 获取推荐目标
        val target = targetCalculator.calculateTargets(user)

        // 识别缺口
        return identifyGaps(averages, target)
    }

    private fun calculateDailyNutritions(records: List<com.example.nutrilog.shared.MealRecord>): Map<String, NutritionFacts> {
        val dailyNutritions = mutableMapOf<String, NutritionFacts>()
        
        records.groupBy { it.date }.forEach { (date, dayRecords) ->
            dailyNutritions[date] = basicNutritionCalculator.calculateDailyNutrition(dayRecords)
        }
        
        return dailyNutritions
    }

    private fun calculateAverages(dailyNutritions: Map<String, NutritionFacts>): Map<String, Double> {
        if (dailyNutritions.isEmpty()) {
            return mapOf(
                "calories" to 0.0,
                "protein" to 0.0,
                "carbs" to 0.0,
                "fat" to 0.0,
                "fiber" to 0.0,
                "sugar" to 0.0,
                "sodium" to 0.0
            )
        }

        val count = dailyNutritions.size.toDouble()
        var totalCalories = 0.0
        var totalProtein = 0.0
        var totalCarbs = 0.0
        var totalFat = 0.0
        var totalFiber = 0.0
        var totalSugar = 0.0
        var totalSodium = 0.0

        dailyNutritions.values.forEach { nutrition ->
            totalCalories += nutrition.calories
            totalProtein += nutrition.protein
            totalCarbs += nutrition.carbs
            totalFat += nutrition.fat
            totalFiber += nutrition.fiber ?: 0.0
            totalSugar += nutrition.sugar ?: 0.0
            totalSodium += nutrition.sodium ?: 0.0
        }

        return mapOf(
            "calories" to (totalCalories / count),
            "protein" to (totalProtein / count),
            "carbs" to (totalCarbs / count),
            "fat" to (totalFat / count),
            "fiber" to (totalFiber / count),
            "sugar" to (totalSugar / count),
            "sodium" to (totalSodium / count)
        )
    }

    private fun identifyGaps(
        averages: Map<String, Double>,
        target: NutritionTarget
    ): List<NutritionalGap> {
        val gaps = mutableListOf<NutritionalGap>()

        // 检查蛋白质
        val proteinAvg = averages["protein"] ?: 0.0
        val proteinGap = calculateGap(proteinAvg, target.protein.min)
        if (proteinGap > 0) {
            gaps.add(createGap("protein", proteinAvg, target.protein.min, proteinGap))
        }

        // 检查膳食纤维
        val fiberAvg = averages["fiber"] ?: 0.0
        val fiberGap = calculateGap(fiberAvg, target.fiber)
        if (fiberGap > 0) {
            gaps.add(createGap("fiber", fiberAvg, target.fiber, fiberGap))
        }

        // 检查其他营养素...

        return gaps.sortedByDescending { it.severity }
    }

    private fun calculateGap(actual: Double, recommended: Double): Double {
        return if (actual < recommended) {
            (recommended - actual) / recommended * 100
        } else {
            0.0
        }
    }

    private fun createGap(
        nutrient: String,
        averageIntake: Double,
        recommended: Double,
        gapPercentage: Double
    ): NutritionalGap {
        val severity = when {
            gapPercentage > 50 -> Severity.SEVERE
            gapPercentage >= 20 -> Severity.MODERATE
            else -> Severity.MILD
        }

        return NutritionalGap(
            nutrient = nutrient,
            averageIntake = averageIntake,
            recommended = recommended,
            gapPercentage = gapPercentage,
            severity = severity
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

    override suspend fun identifyNutritionalGaps(
        userId: Long,
        days: Int
    ): List<NutritionalGap> {
        TODO("Not yet implemented")
    }

    override suspend fun analyzeEatingPatterns(
        userId: Long,
        startDate: String,
        endDate: String
    ): EatingPatternAnalysis {
        TODO("Not yet implemented")
    }

    override suspend fun getHealthScoreHistory(
        userId: Long,
        days: Int
    ): List<DailyScore> {
        TODO("Not yet implemented")
    }
}