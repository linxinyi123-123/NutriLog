package com.example.nutrilog.di

import android.content.Context
import com.example.nutrilog.data.AppDatabase
import com.example.nutrilog.data.dao.FoodDao
import com.example.nutrilog.data.dao.MealRecordDao
import com.example.nutrilog.data.dao.RecordFoodDao
import com.example.nutrilog.data.repository.FoodRepository
import com.example.nutrilog.data.repository.MealRecordRepository
import com.example.nutrilog.ui.viewmodels.MainViewModel

object AppModule {
    
    // 数据库实例（单例）
    private var database: AppDatabase? = null
    
    fun provideDatabase(context: Context): AppDatabase {
        return database ?: AppDatabase.getDatabase(context).also { database = it }
    }
    
    // DAO实例
    fun provideFoodDao(context: Context): FoodDao = provideDatabase(context).foodDao()
    
    fun provideMealRecordDao(context: Context): MealRecordDao = provideDatabase(context).mealRecordDao()
    
    fun provideRecordFoodDao(context: Context): RecordFoodDao = provideDatabase(context).recordFoodDao()
    
    // Repository实例
    fun provideFoodRepository(context: Context): FoodRepository = 
        FoodRepository(provideFoodDao(context))
    
    fun provideMealRecordRepository(context: Context): MealRecordRepository = 
        MealRecordRepository(provideMealRecordDao(context), provideRecordFoodDao(context))
    
    // ViewModel工厂函数
    fun provideMainViewModel(context: Context): MainViewModel {
        val mealRecordRepository = provideMealRecordRepository(context)
        return MainViewModel(mealRecordRepository)
    }
    
    // 清理资源（用于测试）
    fun clear() {
        database = null
    }
}