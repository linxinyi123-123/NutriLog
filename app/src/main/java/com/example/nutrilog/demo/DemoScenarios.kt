package com.example.nutrilog.demo

import com.example.nutrilog.shared.DailyAnalysis
import com.example.nutrilog.shared.DailyPoint
import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.MealRecord
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.TrendAnalysis
import com.example.nutrilog.data.entities.MealLocation
import com.example.nutrilog.data.entities.MealType
import com.example.nutrilog.shared.NutritionTargetFactory
import java.util.Random

// 演示场景类，提供各种演示数据
object DemoScenarios {
    private val random = Random()
    private val nutritionTargetFactory = NutritionTargetFactory()
    
    // 健康饮食示例
    fun createHealthyDayAnalysis(): DailyAnalysis {
        return DailyAnalysis(
            date = "2024-01-15",
            nutrition = NutritionFacts(
                calories = 2200.0,
                protein = 85.0,
                carbs = 280.0,
                fat = 65.0,
                fiber = 28.0,
                sodium = 1800.0
            ),
            target = nutritionTargetFactory.createForAdultMale().let { target ->
                NutritionFacts(
                    calories = target.calories.min,
                    protein = target.protein.min,
                    carbs = target.carbs.min,
                    fat = target.fat.min,
                    fiber = target.fiber,
                    sodium = target.sodium
                )
            },
            score = HealthScore(
                total = 92.0,
                breakdown = mapOf(
                    "calories" to 95.0,
                    "macros" to 90.0,
                    "micros" to 88.0,
                    "regularity" to 96.0,
                    "variety" to 91.0
                ),
                feedback = listOf(
                    "蛋白质摄入充足",
                    "膳食纤维达标",
                    "饮食时间规律"
                )
            ),
            records = createHealthyDayRecords()
        )
    }
    
    // 不健康饮食示例
    fun createUnhealthyDayAnalysis(): DailyAnalysis {
        return DailyAnalysis(
            date = "2024-01-16",
            nutrition = NutritionFacts(
                calories = 3200.0,
                protein = 45.0,
                carbs = 450.0,
                fat = 120.0,
                fiber = 12.0,
                sodium = 3500.0
            ),
            target = nutritionTargetFactory.createForAdultMale().let { target ->
                NutritionFacts(
                    calories = target.calories.min,
                    protein = target.protein.min,
                    carbs = target.carbs.min,
                    fat = target.fat.min,
                    fiber = target.fiber,
                    sodium = target.sodium
                )
            },
            score = HealthScore(
                total = 58.0,
                breakdown = mapOf(
                    "calories" to 40.0,
                    "macros" to 55.0,
                    "micros" to 60.0,
                    "regularity" to 65.0,
                    "variety" to 70.0
                ),
                feedback = listOf(
                    "热量摄入超标",
                    "蛋白质不足",
                    "钠摄入过高"
                )
            ),
            records = createUnhealthyDayRecords()
        )
    }
    
    // 趋势数据示例
    fun createWeeklyTrend(): TrendAnalysis {
        return TrendAnalysis(
            dailyPoints = (8..14).map { day ->
                DailyPoint(
                    date = "2024-01-${if (day < 10) "0$day" else day}",
                    nutrition = NutritionFacts(
                        calories = (2000 + random.nextInt(400)).toDouble(),
                        protein = (70 + random.nextInt(20)).toDouble(),
                        carbs = (250 + random.nextInt(50)).toDouble(),
                        fat = (60 + random.nextInt(15)).toDouble()
                    ),
                    score = (70 + random.nextInt(25)).toDouble()
                )
            }
        )
    }
    
    // 创建健康日的饮食记录
    private fun createHealthyDayRecords(): List<MealRecord> {
        return listOf(
            MealRecord(
                id = 1,
                date = "2024-01-15",
                time = "08:30",
                mealType = MealType.BREAKFAST,
                location = MealLocation.HOME,
                mood = 3,
                foods = emptyList()
            ),
            MealRecord(
                id = 2,
                date = "2024-01-15",
                time = "12:30",
                mealType = MealType.LUNCH,
                location = MealLocation.RESTAURANT,
                mood = 4,
                foods = emptyList()
            ),
            MealRecord(
                id = 3,
                date = "2024-01-15",
                time = "15:30",
                mealType = MealType.SNACK,
                location = MealLocation.OFFICE,
                mood = 3,
                foods = emptyList()
            ),
            MealRecord(
                id = 4,
                date = "2024-01-15",
                time = "18:30",
                mealType = MealType.DINNER,
                location = MealLocation.HOME,
                mood = 5,
                foods = emptyList()
            )
        )
    }
    
    // 创建不健康日的饮食记录
    private fun createUnhealthyDayRecords(): List<MealRecord> {
        return listOf(
            MealRecord(
                id = 1,
                date = "2024-01-16",
                time = "10:00",
                mealType = MealType.BREAKFAST,
                location = MealLocation.HOME,
                mood = 2,
                foods = emptyList()
            ),
            MealRecord(
                id = 2,
                date = "2024-01-16",
                time = "14:00",
                mealType = MealType.LUNCH,
                location = MealLocation.TAKEAWAY,
                mood = 3,
                foods = emptyList()
            ),
            MealRecord(
                id = 3,
                date = "2024-01-16",
                time = "17:00",
                mealType = MealType.SNACK,
                location = MealLocation.OFFICE,
                mood = 2,
                foods = emptyList()
            ),
            MealRecord(
                id = 4,
                date = "2024-01-16",
                time = "20:00",
                mealType = MealType.DINNER,
                location = MealLocation.RESTAURANT,
                mood = 4,
                foods = emptyList()
            )
        )
    }
}
