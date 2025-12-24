package com.example.nutrilog.data.repository

import com.example.nutrilog.data.dao.FoodDao
import com.example.nutrilog.data.entities.FoodCategory
import com.example.nutrilog.data.entities.FoodItem
import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val foodDao: FoodDao
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
}