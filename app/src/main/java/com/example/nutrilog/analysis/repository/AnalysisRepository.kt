package com.example.nutrilog.analysis.repository

import com.example.nutrilog.common.models.UserProfile
import com.example.nutrilog.shared.DailyAnalysis
import com.example.nutrilog.shared.MealRecord

interface AnalysisRepository {
    // 获取用户数据
    suspend fun getUserProfile(): UserProfile?

    // 获取饮食记录
    suspend fun getRecordsByDate(date: String): List<MealRecord>
    suspend fun getRecordsByDateRange(startDate: String, endDate: String): List<MealRecord>

    // 保存分析结果
    suspend fun saveDailyAnalysis(date: String, analysis: DailyAnalysis)
    suspend fun getDailyAnalysis(date: String): DailyAnalysis?

    suspend fun transRecords(records :List<com.example.nutrilog.data.entities.MealRecord>):List<MealRecord>?
}