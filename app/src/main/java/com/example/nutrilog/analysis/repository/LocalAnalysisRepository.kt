package com.example.nutrilog.analysis.repository

import com.example.nutrilog.common.models.UserProfile
import com.example.nutrilog.data.dao.FoodDao
import com.example.nutrilog.data.dao.MealRecordDao
import com.example.nutrilog.data.dao.RecordFoodDao
import com.example.nutrilog.shared.DailyAnalysis
import com.example.nutrilog.shared.FoodItem
import com.example.nutrilog.shared.MealRecord

class LocalAnalysisRepository(
    private val mealRecordDao: MealRecordDao,
    private val foodDao: FoodDao,
    private val recordFoodDao: RecordFoodDao
) : AnalysisRepository {
    override suspend fun getUserProfile(): UserProfile? {
        TODO("Not yet implemented")
    }

    override suspend fun getRecordsByDate(date: String): List<MealRecord> {
        val records = mealRecordDao.getByDate(date)
        return transRecords(records)
    }


    override suspend fun getRecordsByDateRange(
        startDate: String,
        endDate: String
    ): List<MealRecord> {
        val records = mealRecordDao.getByDateRange(startDate,endDate)
        return transRecords(records)

    }

    override suspend fun saveDailyAnalysis(
        date: String,
        analysis: DailyAnalysis
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getDailyAnalysis(date: String): DailyAnalysis? {
        TODO("Not yet implemented")
    }

    override suspend fun transRecords(records :List<com.example.nutrilog.data.entities.MealRecord>):List<MealRecord>{
        val list = mutableListOf<Pair<FoodItem, Double>>()
        val ret =  mutableListOf<MealRecord>()
        for (record in records) {
            val foods = recordFoodDao.getFoodsForRecord(record.id)
            for(food in foods){
                val newfood = food.food
                val fooditem = FoodItem(
                    id = newfood.id,
                    name = newfood.name,
                    category = newfood.category,
                    calories = newfood.calories,
                    protein = newfood.protein,
                    sodium = newfood.sodium,
                    fiber = newfood.fiber,
                    sugar = newfood.sugar,
                    fat = newfood.fat,
                    carbs = newfood.carbs
                )
                val pair = fooditem to food.amount
                list.add(pair)
            }
            val newRecord = MealRecord(
                id = record.id,
                date = record.date,
                time = record.time,
                mealType = record.mealType,
                location = record.location,
                mood = record.mood,
                foods = list
            )
            ret.add(newRecord)
            list.clear()
        }
        return ret
    }
}