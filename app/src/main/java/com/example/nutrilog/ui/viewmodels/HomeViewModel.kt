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
    private val foodRepository: FoodRepository,
    private val trendAnalyzer: com.example.nutrilog.analysis.analyzer.TrendAnalyzer
) : ViewModel() {
    // 使用Flow来观察用户数据
    private val _user = MutableStateFlow<User>(
        User(
            id = "1",
            name = "用户",
            streakDays = 0,
            todayScore = 0
        )
    )
    val user: StateFlow<User> = _user.asStateFlow()

    // 使用Flow来观察今日摘要数据
    private val _todaySummary = MutableStateFlow<TodaySummary>(
        TodaySummary(
            calories = 0,
            protein = 0.0,
            carbs = 0.0,
            fat = 0.0,
            fiber = 0.0,
            mealsCount = 0
        )
    )
    val todaySummary: StateFlow<TodaySummary> = _todaySummary.asStateFlow()

    // 使用Flow来观察健康趋势数据
    private val _weeklyTrend = MutableStateFlow<HealthTrend>(
        HealthTrend(
            days = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日"),
            calories = listOf(0, 0, 0, 0, 0, 0, 0),
            protein = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            carbs = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            fat = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        )
    )
    val weeklyTrend: StateFlow<HealthTrend> = _weeklyTrend.asStateFlow()

    // 使用Flow来观察最近饮食记录
    private val _recentMeals = MutableStateFlow<List<Meal>>(emptyList())
    val recentMeals: StateFlow<List<Meal>> = _recentMeals.asStateFlow()

    init {
        // 加载初始数据
        loadAllData()
    }

    // 加载所有数据
    private fun loadAllData() {
        loadUser()
        loadTodaySummary()
        loadRecentMeals()
        loadWeeklyTrend()
    }

    // 加载用户数据
    private fun loadUser() {
        viewModelScope.launch {
            try {
                // 目前使用默认用户数据，后续可从数据库获取
                val defaultUser = User(
                    id = "1",
                    name = "张三",
                    streakDays = 15,
                    todayScore = 85
                )
                _user.value = defaultUser
            } catch (e: Exception) {
                // 发生错误时，使用默认用户数据
                _user.value = User(
                    id = "1",
                    name = "用户",
                    streakDays = 0,
                    todayScore = 0
                )
            }
        }
    }

    // 从数据库加载今日摘要数据
    private fun loadTodaySummary() {
        viewModelScope.launch {
            try {
                // 获取今日的餐记录
                val todayRecords = mealRecordRepository.getTodayMealRecords()
                
                // 计算今日营养成分总和
                var totalCalories = 0.0
                var totalProtein = 0.0
                var totalCarbs = 0.0
                var totalFat = 0.0
                var totalFiber = 0.0
                
                for (record in todayRecords) {
                    // 获取该记录的食物列表
                    val foodsWithAmount = mealRecordRepository.getFoodsForRecord(record.id)
                    
                    for ((food, amount) in foodsWithAmount) {
                        // 计算单个食物的营养成分：(每100克营养成分 * 克数) / 100
                        totalCalories += (food.calories * amount) / 100
                        totalProtein += (food.protein * amount) / 100
                        totalCarbs += (food.carbs * amount) / 100
                        totalFat += (food.fat * amount) / 100
                        totalFiber += (food.fiber ?: 0.0) * amount / 100
                    }
                }
                
                // 更新今日摘要数据
                _todaySummary.value = TodaySummary(
                    calories = totalCalories.toInt(),
                    protein = totalProtein,
                    carbs = totalCarbs,
                    fat = totalFat,
                    fiber = totalFiber,
                    mealsCount = todayRecords.size
                )
                
                // 计算健康评分
                val nutritionFacts = com.example.nutrilog.shared.NutritionFacts(
                    calories = totalCalories,
                    protein = totalProtein,
                    carbs = totalCarbs,
                    fat = totalFat,
                    fiber = totalFiber
                )
                
                val healthScore = trendAnalyzer.calculateDailyHealthScore(nutritionFacts).toInt()
                
                // 更新用户的健康评分
                _user.value = _user.value.copy(
                    todayScore = healthScore
                )
            } catch (e: Exception) {
                // 发生错误时，使用默认数据
                _todaySummary.value = TodaySummary(
                    calories = 0,
                    protein = 0.0,
                    carbs = 0.0,
                    fat = 0.0,
                    fiber = 0.0,
                    mealsCount = 0
                )
                
                // 发生错误时，使用默认健康评分
                _user.value = _user.value.copy(
                    todayScore = 0
                )
            }
        }
    }

    // 从数据库加载过去7天的趋势数据
    private fun loadWeeklyTrend() {
        viewModelScope.launch {
            try {
                // 获取过去7天的日期
                val today = java.time.LocalDate.now()
                val days = mutableListOf<String>()
                val calorieList = mutableListOf<Int>()
                val proteinList = mutableListOf<Double>()
                val carbsList = mutableListOf<Double>()
                val fatList = mutableListOf<Double>()
                
                // 遍历过去7天
                for (i in 6 downTo 0) {
                    val date = today.minusDays(i.toLong())
                    val dateString = date.toString()
                    days.add(getDayOfWeek(date))
                    
                    // 获取该日期的餐记录
                    val records = mealRecordRepository.getMealRecordsByDate(dateString)
                    
                    // 计算该日期的营养成分总和
                    var dayCalories = 0
                    var dayProtein = 0.0
                    var dayCarbs = 0.0
                    var dayFat = 0.0
                    
                    for (record in records) {
                        // 获取该记录的食物列表
                        val foodsWithAmount = mealRecordRepository.getFoodsForRecord(record.id)
                        
                        for ((food, amount) in foodsWithAmount) {
                            // 计算单个食物的营养成分：(每100克营养成分 * 克数) / 100
                            dayCalories += ((food.calories * amount) / 100).toInt()
                            dayProtein += (food.protein * amount) / 100
                            dayCarbs += (food.carbs * amount) / 100
                            dayFat += (food.fat * amount) / 100
                        }
                    }
                    
                    // 添加到列表
                    calorieList.add(dayCalories)
                    proteinList.add(dayProtein)
                    carbsList.add(dayCarbs)
                    fatList.add(dayFat)
                }
                
                // 更新健康趋势数据
                _weeklyTrend.value = HealthTrend(
                    days = days,
                    calories = calorieList,
                    protein = proteinList,
                    carbs = carbsList,
                    fat = fatList
                )
            } catch (e: Exception) {
                // 发生错误时，使用默认数据
                _weeklyTrend.value = HealthTrend(
                    days = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日"),
                    calories = listOf(0, 0, 0, 0, 0, 0, 0),
                    protein = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    carbs = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                    fat = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                )
            }
        }
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
                    
                    // 根据时间判断餐次类型
                    val mealType = com.example.nutrilog.data.entities.MealType.fromTime(record.time)
                    
                    Meal(
                        id = record.id.toString(),
                        name = mealType.displayName,
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

    // 辅助方法：获取星期几的中文名称
    private fun getDayOfWeek(date: java.time.LocalDate): String {
        val dayOfWeek = date.dayOfWeek.value
        return when (dayOfWeek) {
            1 -> "周一"
            2 -> "周二"
            3 -> "周三"
            4 -> "周四"
            5 -> "周五"
            6 -> "周六"
            7 -> "周日"
            else -> "周一"
        }
    }

    // 刷新所有数据
    fun refreshAllData() {
        loadAllData()
    }

    // 刷新最近饮食记录
    fun refreshRecentMeals() {
        loadRecentMeals()
    }
}