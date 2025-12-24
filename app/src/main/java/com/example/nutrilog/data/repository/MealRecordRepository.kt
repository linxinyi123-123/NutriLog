package com.example.nutrilog.data.repository

import com.example.nutrilog.data.dao.MealRecordDao
import com.example.nutrilog.data.dao.RecordFoodDao
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.entities.MealRecordWithFoods
import com.example.nutrilog.data.entities.MealType
import kotlinx.coroutines.flow.Flow

class MealRecordRepository(
    private val mealRecordDao: MealRecordDao,
    private val recordFoodDao: RecordFoodDao
) {
    // 基础CRUD操作
    suspend fun insertMealRecord(record: MealRecord): Long = mealRecordDao.insert(record)
    
    suspend fun updateMealRecord(record: MealRecord) = mealRecordDao.update(record)
    
    suspend fun deleteMealRecord(record: MealRecord) = mealRecordDao.delete(record)
    
    suspend fun deleteMealRecordById(id: Long) = mealRecordDao.deleteById(id)
    
    // 查询操作
    suspend fun getMealRecordById(id: Long): MealRecord? = mealRecordDao.getById(id)
    
    suspend fun getMealRecordsByDate(date: String): List<MealRecord> = 
        mealRecordDao.getByDate(date)
    
    fun getMealRecordsByDateFlow(date: String): Flow<List<MealRecord>> = 
        mealRecordDao.getByDateFlow(date)
    
    suspend fun getMealRecordsByDateRange(startDate: String, endDate: String): List<MealRecord> = 
        mealRecordDao.getByDateRange(startDate, endDate)
    
    suspend fun getMealRecordsByMealType(mealType: MealType): List<MealRecord> = 
        mealRecordDao.getByMealType(mealType)
    
    suspend fun getRecentMealRecords(limit: Int = 50): List<MealRecord> = 
        mealRecordDao.getRecent(limit)
    
    // 带食物的记录操作
    suspend fun insertMealRecordWithFoods(record: MealRecord, foodIds: List<Long>): Long {
        val recordId = mealRecordDao.insert(record)
        recordFoodDao.insertFoodsForRecord(recordId, foodIds)
        return recordId
    }
    
    // 统计操作
    suspend fun getMealRecordCount(): Int = mealRecordDao.count()
    
    suspend fun getTodayMealRecords(): List<MealRecord> {
        val today = java.time.LocalDate.now().toString()
        return mealRecordDao.getByDate(today)
    }
    
    // 批量操作
    suspend fun insertMultipleMealRecords(records: List<MealRecord>) {
        records.forEach { mealRecordDao.insert(it) }
    }
}