package com.example.nutrilog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_items",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["category"]),
        Index(value = ["pinyin"]) // 为拼音搜索优化
    ]
)
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,                    // 中文名称
    
    @ColumnInfo(name = "english_name")
    val englishName: String? = null,     // 英文名称
    
    @ColumnInfo(name = "pinyin")
    val pinyin: String? = null,          // 拼音，用于搜索
    
    @ColumnInfo(name = "category")
    val category: FoodCategory,          // 使用枚举更规范
    
    @ColumnInfo(name = "sub_category")
    val subCategory: String? = null,     // 子分类，如"叶菜类"
    
    // 营养信息（每100克）
    @ColumnInfo(name = "calories")
    val calories: Double,                // 热量(kcal)
    
    @ColumnInfo(name = "protein")
    val protein: Double,                 // 蛋白质(g)
    
    @ColumnInfo(name = "carbs")
    val carbs: Double,                   // 碳水化合物(g)
    
    @ColumnInfo(name = "fat")
    val fat: Double,                     // 脂肪(g)
    
    @ColumnInfo(name = "fiber")
    val fiber: Double? = null,           // 膳食纤维(g)
    
    @ColumnInfo(name = "sugar")
    val sugar: Double? = null,           // 糖(g)
    
    @ColumnInfo(name = "sodium")
    val sodium: Double? = null,          // 钠(mg)
    
    // 单位信息
    @ColumnInfo(name = "default_unit")
    val defaultUnit: FoodUnit = FoodUnit.GRAMS,  // 默认单位(基于营养信息）
    
    @ColumnInfo(name = "default_amount")
    val defaultAmount: Double = 100.0,           // 默认份量
    
    // 视觉信息
    @ColumnInfo(name = "color")
    val color: String? = null,           // 代表色，如"#4CAF50"
    
    @ColumnInfo(name = "icon")
    val icon: String? = null,            // 图标资源名
    
    // 元数据
    @ColumnInfo(name = "is_common")
    val isCommon: Boolean = true,        // 是否常见食物
    
    @ColumnInfo(name = "is_processed")
    val isProcessed: Boolean = false,    // 是否加工食品
    
    @ColumnInfo(name = "tags")
    val tags: String? = null,            // 标签，逗号分隔，如"辣,咸,油炸"
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    // 辅助方法：计算任意份量的营养
    fun calculateNutrition(amount: Double, unit: FoodUnit = this.defaultUnit): NutritionFacts {
        val amountInGrams = convertToGrams(amount, unit)
        val ratio = amountInGrams / defaultAmount
        
        return NutritionFacts(
            calories = calories * ratio,
            protein = protein * ratio,
            carbs = carbs * ratio,
            fat = fat * ratio,
            fiber = fiber?.times(ratio),
            sugar = sugar?.times(ratio),
            sodium = sodium?.times(ratio)
        )
    }
    
    private fun convertToGrams(amount: Double, unit: FoodUnit): Double {
        return when (unit) {
            FoodUnit.GRAMS -> amount
            FoodUnit.MILLILITERS -> amount * 1.0  // 假设密度为1g/ml
            FoodUnit.PIECES -> amount * defaultAmount  // 按个计算
            FoodUnit.BOWLS -> amount * 250.0      // 假设一碗250g
            FoodUnit.SPOONS -> amount * 15.0      // 假设一勺15g
            FoodUnit.CUSTOM -> amount
        }
    }
}