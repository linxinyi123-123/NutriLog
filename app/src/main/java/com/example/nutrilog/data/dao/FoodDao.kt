package com.example.nutrilog.data.dao

import androidx.room.*
import com.example.nutrilog.data.entities.FoodCategory
import com.example.nutrilog.data.entities.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    // 基础CRUD操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: FoodItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<FoodItem>)
    
    @Update
    suspend fun update(food: FoodItem)
    
    @Delete
    suspend fun delete(food: FoodItem)
    
    @Query("DELETE FROM food_items")
    suspend fun deleteAll()
    
    // 统计操作
    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun count(): Int
    
    // 查询操作
    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getById(id: Long): FoodItem?
    
    @Query("SELECT * FROM food_items ORDER BY name")
    fun getAll(): Flow<List<FoodItem>>
    
    // 搜索功能（多种方式）
    @Query(""" 
        SELECT * FROM food_items 
        WHERE name LIKE '%' || :query || '%' 
           OR english_name LIKE '%' || :query || '%' 
           OR pinyin LIKE :query || '%' 
        ORDER BY 
            CASE 
                WHEN name LIKE :query || '%' THEN 1 
                WHEN english_name LIKE :query || '%' THEN 2 
                WHEN pinyin LIKE :query || '%' THEN 3 
                ELSE 4 
            END, 
            name 
        LIMIT :limit 
    """)
    suspend fun search(query: String, limit: Int = 20): List<FoodItem>
    
    // 分类查询
    @Query("SELECT * FROM food_items WHERE category = :category ORDER BY name")
    suspend fun getByCategory(category: FoodCategory): List<FoodItem>
    
    @Query("SELECT * FROM food_items WHERE category IN (:categories) ORDER BY name")
    suspend fun getByCategories(categories: List<FoodCategory>): List<FoodItem>
    
    // 常用食物查询
    @Query("SELECT * FROM food_items WHERE is_common = 1 ORDER BY name")
    suspend fun getCommonFoods(): List<FoodItem>
    
    // 最近使用食物（需要关联查询，这里简化）
    @Query(""" 
        SELECT fi.*, COUNT(rfi.food_id) as usage_count 
        FROM food_items fi 
        LEFT JOIN record_food_items rfi ON fi.id = rfi.food_id 
        GROUP BY fi.id 
        ORDER BY usage_count DESC, fi.name 
        LIMIT :limit 
    """)
    suspend fun getRecentlyUsed(limit: Int = 10): List<FoodItem>
    
    // 获取所有分类
    @Query("SELECT DISTINCT category FROM food_items ORDER BY category")
    suspend fun getAllCategories(): List<FoodCategory>
}