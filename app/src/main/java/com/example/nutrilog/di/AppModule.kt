package com.example.nutrilog.di

import android.content.Context
import com.example.nutrilog.data.AppDatabase
import com.example.nutrilog.data.dao.FoodDao
import com.example.nutrilog.data.dao.MealRecordDao
import com.example.nutrilog.data.dao.RecordFoodDao
import com.example.nutrilog.data.dao.FoodComboDao
import com.example.nutrilog.data.dao.ComboFoodDao
import com.example.nutrilog.data.repository.FoodRepository
import com.example.nutrilog.data.repository.MealRecordRepository
import com.example.nutrilog.ui.viewmodels.MainViewModel
import com.example.nutrilog.ui.viewmodels.AddRecordViewModel
import com.example.nutrilog.ui.viewmodels.ReportViewModel
import com.example.nutrilog.ui.viewmodels.ReportPreviewViewModel
import com.example.nutrilog.analysis.repository.AnalysisRepository
import com.example.nutrilog.analysis.repository.LocalAnalysisRepository
import com.example.nutrilog.analysis.service.LazyAnalysisService
import com.example.nutrilog.analysis.analysis.AnalysisCache
import com.example.nutrilog.analysis.calculator.BasicNutritionCalculator
import com.example.nutrilog.analysis.calculator.HealthScoreCalculatorV3
import com.example.nutrilog.analysis.analyzer.MealPatternAnalyzer
import com.example.nutrilog.analysis.analyzer.FoodVarietyAnalyzer
import com.example.nutrilog.analysis.analyzer.TrendAnalyzer

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
    
    fun provideFoodComboDao(context: Context): FoodComboDao = provideDatabase(context).foodComboDao()    
    
    fun provideComboFoodDao(context: Context): ComboFoodDao = provideDatabase(context).comboFoodDao()    
    
    // Repository实例
    fun provideFoodRepository(context: Context): FoodRepository = 
        FoodRepository(
            provideFoodDao(context),
            provideFoodComboDao(context),
            provideComboFoodDao(context)
        )    
    
    fun provideMealRecordRepository(context: Context): MealRecordRepository = 
        MealRecordRepository(provideMealRecordDao(context), provideRecordFoodDao(context))    
    
    // 分析模块依赖
    fun provideAnalysisRepository(context: Context): AnalysisRepository = 
        LocalAnalysisRepository(
            mealRecordDao = provideMealRecordDao(context),
            foodDao = provideFoodDao(context),
            recordFoodDao = provideRecordFoodDao(context)
        )
    
    fun provideAnalysisCache(): AnalysisCache = AnalysisCache()
    
    fun provideBasicNutritionCalculator(): BasicNutritionCalculator = BasicNutritionCalculator()
    
    fun provideHealthScoreCalculator(): HealthScoreCalculatorV3 = HealthScoreCalculatorV3(
        target = com.example.nutrilog.shared.NutritionTargetFactory().createForAdultMale(),
        patternAnalyzer = provideMealPatternAnalyzer(),
        varietyAnalyzer = provideFoodVarietyAnalyzer()
    )
    
    fun provideMealPatternAnalyzer(): MealPatternAnalyzer = MealPatternAnalyzer()
    
    fun provideFoodVarietyAnalyzer(): FoodVarietyAnalyzer = FoodVarietyAnalyzer()
    
    fun provideTrendAnalyzer(): TrendAnalyzer = TrendAnalyzer()
    
    fun provideLazyAnalysisService(context: Context): LazyAnalysisService = 
        LazyAnalysisService(
            repository = provideAnalysisRepository(context),
            cache = provideAnalysisCache(),
            nutritionCalculator = provideBasicNutritionCalculator(),
            scoreCalculator = provideHealthScoreCalculator(),
            patternAnalyzer = provideMealPatternAnalyzer(),
            varietyAnalyzer = provideFoodVarietyAnalyzer()
        )
    
    // ViewModel工厂函数
    fun provideMainViewModel(context: Context): MainViewModel {
        val mealRecordRepository = provideMealRecordRepository(context)
        val foodRepository = provideFoodRepository(context)
        return MainViewModel(mealRecordRepository, foodRepository)
    }    
    
    fun provideAddRecordViewModel(context: Context): AddRecordViewModel {
        val mealRecordRepository = provideMealRecordRepository(context)
        val foodRepository = provideFoodRepository(context)
        return AddRecordViewModel(mealRecordRepository, foodRepository)
    }    
    fun provideReportViewModel(): ReportViewModel {
        return ReportViewModel()
    }    
    
    fun provideReportPreviewViewModel(): ReportPreviewViewModel {
        return ReportPreviewViewModel()
    }    
    
    // 清理资源（用于测试）
    fun clear() {
        database = null
    }
}