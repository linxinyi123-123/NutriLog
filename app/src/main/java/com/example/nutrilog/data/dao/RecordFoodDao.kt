package com.example.nutrilog.data.dao

import androidx.room.*
import com.example.nutrilog.data.entities.FoodItemWithAmount
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.entities.RecordFoodItem

@Dao
interface RecordFoodDao {
    // 插入关联
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recordFood: RecordFoodItem)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recordFoods: List<RecordFoodItem>)
    
    // 删除关联
    @Delete
    suspend fun delete(recordFood: RecordFoodItem)
    
    @Query("DELETE FROM record_food_items WHERE record_id = :recordId")
    suspend fun deleteByRecordId(recordId: Long)
    
    @Query("DELETE FROM record_food_items WHERE food_id = :foodId")
    suspend fun deleteByFoodId(foodId: Long)
    
    // 查询记录的食物
    @Query(""" 
        SELECT fi.*, rfi.amount, rfi.unit, rfi.note 
        FROM food_items fi 
        INNER JOIN record_food_items rfi ON fi.id = rfi.food_id 
        WHERE rfi.record_id = :recordId 
        ORDER BY rfi.order_index 
    """)
    suspend fun getFoodsForRecord(recordId: Long): List<FoodItemWithAmount>
    
    // 查询食物被哪些记录使用
    @Query(""" 
        SELECT mr.* 
        FROM meal_records mr 
        INNER JOIN record_food_items rfi ON mr.id = rfi.record_id 
        WHERE rfi.food_id = :foodId 
        ORDER BY mr.date DESC, mr.time DESC 
    """)
    suspend fun getRecordsForFood(foodId: Long): List<MealRecord>
    
    // 批量操作
    @Transaction
    suspend fun replaceFoodsForRecord(recordId: Long, foods: List<RecordFoodItem>) {
        deleteByRecordId(recordId)
        insertAll(foods)
    }
}