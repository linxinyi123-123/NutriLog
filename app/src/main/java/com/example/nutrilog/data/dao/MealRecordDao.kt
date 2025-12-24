package com.example.nutrilog.data.dao

import androidx.room.*
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.entities.MealType
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.NotNull

@Dao
interface MealRecordDao {
    // 基础CRUD
    @Insert
    suspend fun insert(record: MealRecord): Long
    
    @Update
    suspend fun update(record: MealRecord)
    
    @Delete
    suspend fun delete(record: MealRecord)
    
    @Query("DELETE FROM meal_records WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    // 查询单个记录
    @Query("SELECT * FROM meal_records WHERE id = :id")
    suspend fun getById(id: Long): MealRecord?
    
    // 查询某天所有记录
    @Query("SELECT * FROM meal_records WHERE date = :date ORDER BY time")
    suspend fun getByDate(date: String): List<MealRecord>
    
    @Query("SELECT * FROM meal_records WHERE date = :date ORDER BY time")
    fun getByDateFlow(date: String): Flow<List<MealRecord>>
    
    // 查询日期范围记录
    @Query(""" 
        SELECT * FROM meal_records 
        WHERE date BETWEEN :startDate AND :endDate 
        ORDER BY date DESC, time DESC 
    """)
    suspend fun getByDateRange(startDate: String, endDate: String): List<MealRecord>
    
    // 查询餐次记录
    @Query("SELECT * FROM meal_records WHERE meal_type = :mealType ORDER BY date DESC, time DESC")
    suspend fun getByMealType(mealType: @org.jetbrains.annotations.NotNull MealType): List<MealRecord>
    
    // 最近记录
    @Query("SELECT * FROM meal_records ORDER BY created_at DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 50): List<MealRecord>
    
    @Query("SELECT * FROM meal_records ORDER BY created_at DESC LIMIT :limit")
    fun getRecentFlow(limit: Int = 50): Flow<List<MealRecord>>
    
    // 统计
    @Query("SELECT COUNT(*) FROM meal_records WHERE date = :date")
    suspend fun countByDate(date: String): Int
    
    @Query("SELECT COUNT(*) FROM meal_records")
    suspend fun countAll(): Int
    
    // 获取有记录的所有日期
    @Query("SELECT DISTINCT date FROM meal_records ORDER BY date DESC")
    suspend fun getAllDates(): List<String>
}