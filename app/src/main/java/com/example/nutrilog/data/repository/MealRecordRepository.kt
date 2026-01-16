package com.example.nutrilog.data.repository

import com.example.nutrilog.data.dao.MealRecordDao
import com.example.nutrilog.data.dao.RecordFoodDao
import com.example.nutrilog.data.entities.FoodItem
import com.example.nutrilog.data.entities.FoodItemWithAmount
import com.example.nutrilog.data.entities.FoodUnit
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.entities.MealRecordWithFoods
import com.example.nutrilog.data.entities.MealType
import com.example.nutrilog.data.entities.RecordFoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MealRecordRepository(
    private val mealRecordDao: MealRecordDao,
    private val recordFoodDao: RecordFoodDao
) {
    private val _recordUpdates = MutableSharedFlow<com.example.nutrilog.analysis.listener.RecordUpdate>()
    val recordUpdates = _recordUpdates.asSharedFlow()

    fun recordUpdates(): Flow<com.example.nutrilog.analysis.listener.RecordUpdate> = _recordUpdates.asSharedFlow()

    // 基础CRUD操作
    suspend fun insertMealRecord(record: MealRecord): Long {
        val id = mealRecordDao.insert(record)
        _recordUpdates.emit(com.example.nutrilog.analysis.listener.RecordUpdate.RecordAdded(record))
        return id
    }

    suspend fun updateMealRecord(record: MealRecord) {
        mealRecordDao.update(record)
        _recordUpdates.emit(com.example.nutrilog.analysis.listener.RecordUpdate.RecordUpdated(record))
    }

    suspend fun deleteMealRecord(record: MealRecord) {
        mealRecordDao.delete(record)
        _recordUpdates.emit(com.example.nutrilog.analysis.listener.RecordUpdate.RecordDeleted(record.date))
    }

    suspend fun deleteMealRecordById(id: Long) {
        val record = mealRecordDao.getById(id)
        if (record != null) {
            mealRecordDao.deleteById(id)
            _recordUpdates.emit(com.example.nutrilog.analysis.listener.RecordUpdate.RecordDeleted(record.date))
        }
    }
    
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
        _recordUpdates.emit(com.example.nutrilog.analysis.listener.RecordUpdate.RecordAdded(record))
        return recordId
    }
    
    // 添加记录并关联食物（带数量）
    suspend fun addMealRecordWithFoods(record: MealRecord, foods: List<Pair<FoodItem, Double>>): Long {
        val recordId = mealRecordDao.insert(record)
        
        // 创建食物关联
        val recordFoods = foods.mapIndexed { index, (food, quantity) ->
            RecordFoodItem(
                recordId = recordId,
                foodId = food.id,
                amount = quantity,
                unit = FoodUnit.GRAMS,
                orderIndex = index
            )
        }
        
        // 保存食物关联
        recordFoodDao.insertAll(recordFoods)
        _recordUpdates.emit(com.example.nutrilog.analysis.listener.RecordUpdate.RecordAdded(record))
        return recordId
    }
    
    // 统计操作
    suspend fun getMealRecordCount(): Int = mealRecordDao.count()
    
    suspend fun getTodayMealRecords(): List<MealRecord> {
        val today = java.time.LocalDate.now().toString()
        return mealRecordDao.getByDate(today)
    }
    
    // 获取记录的食物列表（带数量）
    suspend fun getFoodsForRecord(recordId: Long): List<Pair<FoodItem, Double>> {
        val foodsWithAmount = recordFoodDao.getFoodsForRecord(recordId)
        return foodsWithAmount.map { it.food to it.amount }
    }
    
    // 更新记录并关联食物（带数量）
    suspend fun updateMealRecordWithFoods(record: MealRecord, foods: List<Pair<FoodItem, Double>>) {
        // 更新记录信息
        mealRecordDao.update(record)
        
        // 删除旧的关联食物
        recordFoodDao.deleteByRecordId(record.id)
        
        // 创建新的食物关联
        val recordFoods = foods.mapIndexed { index, (food, quantity) ->
            RecordFoodItem(
                recordId = record.id,
                foodId = food.id,
                amount = quantity,
                unit = FoodUnit.GRAMS,
                orderIndex = index
            )
        }
        
        // 保存新的食物关联
        recordFoodDao.insertAll(recordFoods)
        _recordUpdates.emit(com.example.nutrilog.analysis.listener.RecordUpdate.RecordUpdated(record))
    }
}