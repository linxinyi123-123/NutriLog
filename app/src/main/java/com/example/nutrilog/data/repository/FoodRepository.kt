package com.example.nutrilog.data.repository

import com.example.nutrilog.data.dao.ComboFoodDao
import com.example.nutrilog.data.dao.FoodComboDao
import com.example.nutrilog.data.dao.FoodDao
import com.example.nutrilog.data.entities.ComboFood
import com.example.nutrilog.data.entities.FoodCategory
import com.example.nutrilog.data.entities.FoodCombo
import com.example.nutrilog.data.entities.FoodItem
import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val foodDao: FoodDao,
    private val foodComboDao: FoodComboDao,
    private val comboFoodDao: ComboFoodDao
) {
    // 基础CRUD操作
    suspend fun insertFood(food: FoodItem): Long = foodDao.insert(food)
    
    suspend fun insertAllFoods(foods: List<FoodItem>) = foodDao.insertAll(foods)
    
    suspend fun updateFood(food: FoodItem) = foodDao.update(food)
    
    suspend fun deleteFood(food: FoodItem) = foodDao.delete(food)
    
    suspend fun deleteAllFoods() = foodDao.deleteAll()
    
    // 查询操作
    suspend fun getFoodById(id: Long): FoodItem? = foodDao.getById(id)
    
    fun getAllFoods(): Flow<List<FoodItem>> = foodDao.getAll()
    
    suspend fun searchFoods(query: String, limit: Int = 10): List<FoodItem> = 
        foodDao.search(query, limit)
    
    suspend fun getFoodsByCategory(category: FoodCategory): List<FoodItem> = 
        foodDao.getByCategory(category)
    
    suspend fun getCommonFoods(): List<FoodItem> = foodDao.getCommonFoods()
    
    suspend fun getRecentlyUsedFoods(limit: Int = 10): List<FoodItem> = 
        foodDao.getRecentlyUsed(limit)
    
    suspend fun getFoodCount(): Int = foodDao.count()
    
    suspend fun getAllCategories(): List<FoodCategory> = foodDao.getAllCategories()
    
    // 批量操作
    suspend fun insertOrUpdateFoods(foods: List<FoodItem>) {
        foodDao.insertAll(foods)
    }
    
    // 组合相关操作
    
    // 创建组合
    suspend fun createFoodCombo(combo: FoodCombo, foods: List<ComboFood>): Long {
        val comboId = foodComboDao.insert(combo)
        foods.forEach { comboFood ->
            comboFoodDao.insert(comboFood.copy(comboId = comboId))
        }
        return comboId
    }
    
    // 更新组合
    suspend fun updateFoodCombo(combo: FoodCombo, foods: List<ComboFood>) {
        foodComboDao.update(combo)
        comboFoodDao.deleteByComboId(combo.id)
        foods.forEach { comboFood ->
            comboFoodDao.insert(comboFood.copy(comboId = combo.id))
        }
    }
    
    // 删除组合
    suspend fun deleteFoodCombo(combo: FoodCombo) {
        foodComboDao.delete(combo)
    }
    
    // 查询所有组合
    fun getAllFoodCombos(): Flow<List<FoodCombo>> = foodComboDao.getAllCombos()
    
    // 根据ID查询组合
    suspend fun getFoodComboById(id: Long): FoodCombo? = foodComboDao.getComboById(id)
    
    // 查询组合的食物列表
    suspend fun getFoodsByComboId(comboId: Long): List<ComboFood> = 
        comboFoodDao.getFoodsByComboId(comboId)
    
    // 获取组合的详细信息（组合+食物列表）
    data class FoodComboWithFoods(
        val combo: FoodCombo,
        val foods: List<ComboFood>
    )
    
    suspend fun getFoodComboWithFoods(comboId: Long): FoodComboWithFoods? {
        val combo = foodComboDao.getComboById(comboId) ?: return null
        val foods = comboFoodDao.getFoodsByComboId(comboId)
        return FoodComboWithFoods(combo, foods)
    }
}