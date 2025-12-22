package com.example.nutrilog.data

import android.content.Context
import android.util.Log
import com.example.nutrilog.data.dao.FoodDao
import com.example.nutrilog.data.entities.FoodItem
import com.example.nutrilog.data.entities.FoodCategory
import com.example.nutrilog.data.entities.FoodUnit
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.first
import java.lang.reflect.Type

class DatabaseInitializer(
    private val context: Context,
    private val foodDao: FoodDao
) {
    
    suspend fun initializeDatabase() {
        Log.d("DatabaseInitializer", "开始初始化数据库...")
        
        val foodCount = foodDao.count()
        
        if (foodCount == 0) {
            Log.d("DatabaseInitializer", "数据库为空，开始填充初始数据")
            loadInitialFoodData()
        } else {
            Log.d("DatabaseInitializer", "数据库已有 $foodCount 条食物记录")
        }
        
        Log.d("DatabaseInitializer", "数据库初始化完成")
    }
    
    private suspend fun loadInitialFoodData() {
        try {
            val jsonString = loadJsonFromAssets("foods.json")
            val foodDataList = parseFoodJson(jsonString)
            foodDao.insertAll(foodDataList)
            
            Log.d("DatabaseInitializer", "成功插入 ${foodDataList.size} 条食物记录")
            
            addPinyinToFoods()
            
        } catch (e: Exception) {
            Log.e("DatabaseInitializer", "加载初始数据失败", e)
            createBasicFoodData()
        }
    }
    
    private fun loadJsonFromAssets(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private fun parseFoodJson(jsonString: String): List<FoodItem> {
        val gson = GsonBuilder()
            .registerTypeAdapter(FoodCategory::class.java, FoodCategoryDeserializer())
            .create()
        
        val foodJsonList = gson.fromJson(jsonString, Array<FoodJson>::class.java)
        
        return foodJsonList.mapIndexed { index, json ->
            FoodItem(
                id = json.id ?: (index + 1).toLong(),
                name = json.name,
                englishName = json.englishName,
                pinyin = convertToPinyin(json.name),
                category = json.category,
                subCategory = json.subCategory,
                calories = json.calories,
                protein = json.protein,
                carbs = json.carbs,
                fat = json.fat,
                fiber = json.fiber,
                sugar = json.sugar,
                sodium = json.sodium,
                defaultUnit = json.defaultUnit ?: FoodUnit.GRAMS,
                defaultAmount = json.defaultAmount ?: 100.0,
                color = json.color,
                icon = json.icon,
                isCommon = json.isCommon ?: true,
                isProcessed = json.isProcessed ?: false,
                tags = json.tags?.joinToString(","),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }
    }
    
    private fun convertToPinyin(chinese: String): String {
        return chinese
    }
    
    private suspend fun addPinyinToFoods() {
        val allFoods = foodDao.getAll().first()
        
        allFoods.forEach { food ->
            if (food.pinyin.isNullOrEmpty()) {
                val pinyin = convertToPinyin(food.name)
                val updatedFood = food.copy(pinyin = pinyin)
                foodDao.update(updatedFood)
            }
        }
    }
    
    private suspend fun createBasicFoodData() {
        val basicFoods = listOf(
            FoodItem(
                name = "米饭",
                category = FoodCategory.GRAINS,
                calories = 116.0,
                protein = 2.6,
                carbs = 25.9,
                fat = 0.3,
                defaultUnit = FoodUnit.BOWLS,
                defaultAmount = 150.0,
                isCommon = true
            ),
            FoodItem(
                name = "鸡蛋",
                category = FoodCategory.PROTEIN,
                calories = 155.0,
                protein = 13.0,
                carbs = 1.1,
                fat = 11.0,
                defaultUnit = FoodUnit.PIECES,
                defaultAmount = 50.0,
                isCommon = true
            )
        )
        
        foodDao.insertAll(basicFoods)
        Log.d("DatabaseInitializer", "创建了 ${basicFoods.size} 条基础食物记录")
    }
    
    data class FoodJson(
        val id: Long? = null,
        val name: String,
        val englishName: String? = null,
        val category: FoodCategory,
        val subCategory: String? = null,
        val calories: Double,
        val protein: Double,
        val carbs: Double,
        val fat: Double,
        val fiber: Double? = null,
        val sugar: Double? = null,
        val sodium: Double? = null,
        val defaultUnit: FoodUnit? = null,
        val defaultAmount: Double? = null,
        val color: String? = null,
        val icon: String? = null,
        val isCommon: Boolean? = null,
        val isProcessed: Boolean? = null,
        val tags: List<String>? = null
    )
    
    class FoodCategoryDeserializer : JsonDeserializer<FoodCategory> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): FoodCategory {
            return try {
                FoodCategory.valueOf(json.asString.uppercase())
            } catch (e: Exception) {
                FoodCategory.OTHERS
            }
        }
    }
}