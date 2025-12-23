package com.example.nutrilog.shared

data class FoodItem(
    val id: Long,
    val name: String,
    val category: String,  // 主食、蔬菜、水果、肉类、豆制品、奶制品、油脂、零食
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
    val mealType: String,  // breakfast, lunch, dinner, snack
    val location: String,
    val mood: Int,
    val foods: List<Pair<FoodItem, Double>>  // 食物和份量(克)
)