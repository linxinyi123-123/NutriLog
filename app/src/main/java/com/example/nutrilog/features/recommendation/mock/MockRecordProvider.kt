// app/src/main/java/com/nutrilog/features/recommendation/mock/MockRecordProvider.kt
package com.example.nutrilog.features.recommendation.mock

import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.features.recommendation.interfaces.RecordProvider
import java.time.LocalDate

class MockRecordProvider : RecordProvider {
    override suspend fun getUserRecords(userId: Long, days: Int): List<MealRecord> {
        return emptyList() // 简化为空列表
    }

    override suspend fun getTodayRecords(userId: Long): List<MealRecord> {
        return emptyList()
    }

    override suspend fun getStreakDays(userId: Long): Int {
        return 5 // 模拟连续5天
    }

    override suspend fun getFoodVarietyCount(userId: Long, days: Int): Int {
        return 8 // 模拟8种食物
    }
}