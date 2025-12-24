package com.example.nutrilog.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.entities.MealType
import com.example.nutrilog.data.entities.MealLocation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 创建用于UI显示的简化模型
data class User(
    val id: String,
    val name: String,
    val streakDays: Int,
    val todayScore: Int
)

data class TodaySummary(
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val mealsCount: Int
)

data class HealthTrend(
    val days: List<String>,
    val calories: List<Int>,
    val protein: List<Double>,
    val carbs: List<Double>,
    val fat: List<Double>
)

data class Meal(
    val id: String,
    val name: String,
    val calories: Int,
    val time: String,
    val imageUrl: String?
)

class HomeViewModel : ViewModel() {
    // 模拟用户数据
    val user = User(
        id = "1",
        name = "张三",
        streakDays = 15,
        todayScore = 85
    )

    // 模拟今日摘要数据
    val todaySummary = TodaySummary(
        calories = 1850,
        protein = 85.5,
        carbs = 220.0,
        fat = 58.0,
        fiber = 25.0,
        mealsCount = 3
    )

    // 模拟健康趋势数据
    val weeklyTrend = HealthTrend(
        days = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日"),
        calories = listOf(1900, 1850, 1780, 1950, 1820, 2000, 1850),
        protein = listOf(80.0, 85.5, 78.0, 90.0, 82.5, 88.0, 85.5),
        carbs = listOf(230.0, 220.0, 210.0, 240.0, 215.0, 250.0, 220.0),
        fat = listOf(60.0, 58.0, 55.0, 62.0, 57.0, 65.0, 58.0)
    )

    // 模拟最近饮食记录
    val recentMeals = listOf(
        Meal(
            id = "1",
            name = "早餐 - 燕麦粥",
            calories = 350,
            time = "08:30",
            imageUrl = null
        ),
        Meal(
            id = "2",
            name = "午餐 - 鸡胸肉沙拉",
            calories = 450,
            time = "12:30",
            imageUrl = null
        ),
        Meal(
            id = "3",
            name = "下午 - 苹果",
            calories = 95,
            time = "15:30",
            imageUrl = null
        )
    )
}