package com.example.nutrilog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.repository.MealRecordRepository
import com.example.nutrilog.data.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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

class HomeViewModel(
    private val mealRecordRepository: MealRecordRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {
    // 模拟用户数据（暂时保留，后续可从数据库获取）
    val user = User(
        id = "1",
        name = "张三",
        streakDays = 15,
        todayScore = 85
    )

    // 模拟今日摘要数据（暂时保留，后续可从数据库获取）
    val todaySummary = TodaySummary(
        calories = 1850,
        protein = 85.5,
        carbs = 220.0,
        fat = 58.0,
        fiber = 25.0,
        mealsCount = 3
    )

    // 模拟健康趋势数据（暂时保留，后续可从数据库获取）
    val weeklyTrend = HealthTrend(
        days = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日"),
        calories = listOf(1900, 1850, 1780, 1950, 1820, 2000, 1850),
        protein = listOf(80.0, 85.5, 78.0, 90.0, 82.5, 88.0, 85.5),
        carbs = listOf(230.0, 220.0, 210.0, 240.0, 215.0, 250.0, 220.0),
        fat = listOf(60.0, 58.0, 55.0, 62.0, 57.0, 65.0, 58.0)
    )

    // 使用Flow来观察最近饮食记录
    private val _recentMeals = MutableStateFlow<List<Meal>>(emptyList())
    val recentMeals: StateFlow<List<Meal>> = _recentMeals.asStateFlow()

    init {
        // 加载最近饮食记录
        loadRecentMeals()
    }

    // 从数据库加载最近饮食记录
    private fun loadRecentMeals() {
        viewModelScope.launch {
            try {
                val records = mealRecordRepository.getRecentMealRecords(limit = 3)
                _recentMeals.value = records.map { record ->
                    // 从数据库获取该记录的食物列表
                    val foodsWithAmount = mealRecordRepository.getFoodsForRecord(record.id)
                    
                    // 计算总卡路里
                    val totalCalories = if (foodsWithAmount.isNotEmpty()) {
                        foodsWithAmount.sumOf { (food, amount) ->
                            // 计算单个食物的卡路里：(每100克卡路里 * 克数) / 100
                            (food.calories * amount) / 100
                        }.toInt()
                    } else {
                        0
                    }
                    
                    Meal(
                        id = record.id.toString(),
                        name = "${record.mealType.displayName} - ${record.location.displayName}",
                        calories = totalCalories,
                        time = record.time,
                        imageUrl = null
                    )
                }
            } catch (e: Exception) {
                // 发生错误时，使用空列表
                _recentMeals.value = emptyList()
            }
        }
    }

    // 刷新最近饮食记录
    fun refreshRecentMeals() {
        loadRecentMeals()
    }
}