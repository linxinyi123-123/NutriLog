package com.example.nutrilog.shared

import com.example.nutrilog.data.entities.FoodCategory
import com.example.nutrilog.data.entities.MealLocation
import com.example.nutrilog.data.entities.MealType

data class FoodItem(
    val id: Long,
    val name: String,
    val category: FoodCategory,  // 主食、蔬菜、水果、肉类、豆制品、奶制品、油脂、零食
    val calories: Double,  // 每100克热量(kcal)
    val protein: Double,   // 蛋白质(g)
    val carbs: Double,     // 碳水化合物(g)
    val fat: Double,       // 脂肪(g)
    val sodium: Double? = null,    // 钠(mg)
    val fiber: Double? = null,     // 膳食纤维(g)
    val sugar: Double? = null      // 糖(g)
)

data class MealRecord(
    val id: Long,
    val date: String,      // yyyy-MM-dd
    val time: String,      // HH:mm
    val mealType: MealType,  // breakfast, lunch, dinner, snack
    val location: MealLocation,
    val mood: Int,
    val foods: List<Pair<FoodItem, Double>>  // 食物和份量(克)
)

// 分析模块数据模型
data class DailyAnalysis(
    val date: String,
    val score: HealthScore,
    val nutrition: NutritionFacts,
    val target: NutritionTarget,
    val records: List<MealRecord>
)

data class HealthScore(
    val total: Double,
    val breakdown: Map<String, Double>,
    val feedback: List<String>
)

data class NutritionFacts(
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double? = null,
    val sugar: Double? = null,
    val sodium: Double?= null
)

//推荐膳食标准
data class NutritionTarget(
    val calories: Range,      // 热量范围
    val protein: Range,       // 蛋白质范围(g)
    val carbs: Range,         // 碳水化合物范围(g)
    val fat: Range,           // 脂肪范围(g)
    val sodium: Double,       // 钠上限(mg)
    val fiber: Double,        // 膳食纤维下限(g)
    val sugar: Double         // 添加糖上限(g)
)

data class Range(val min: Double, val max: Double)

// 基于中国居民膳食指南的标准
class NutritionTargetFactory {
    fun createForAdultMale(): NutritionTarget {
        return NutritionTarget(
            calories = Range(2250.0, 3000.0),
            protein = Range(65.0, 90.0),      // 1.0-1.2g/kg体重
            carbs = Range(338.0, 488.0),      // 50%-65%热量
            fat = Range(50.0, 83.0),          // 20%-30%热量
            sodium = 2000.0,                  // 5克盐约等于2000mg钠
            fiber = 25.0,                     // 每日25克膳食纤维
            sugar = 50.0                      // 每日不超过50克添加糖
        )
    }
}