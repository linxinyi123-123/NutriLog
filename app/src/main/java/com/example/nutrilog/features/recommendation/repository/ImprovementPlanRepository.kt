// ImprovementPlanRepository.kt（改为接口）
package com.example.nutrilog.features.recommendation.repository

import com.example.nutrilog.features.recommendation.model.improvement.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ImprovementPlanRepository {
    suspend fun savePlan(plan: ImprovementPlan)
    fun getUserPlans(userId: Long): Flow<List<ImprovementPlan>>
    fun getActivePlans(userId: Long): Flow<List<ImprovementPlan>>
    suspend fun getPlan(userId: Long, planId: String): ImprovementPlan?
    suspend fun updatePlanProgress(planId: String, progress: Float)
    suspend fun markWeekCompleted(planId: String, weekNumber: Int, completedWeeks: Set<Int>)
    suspend fun deletePlan(planId: String)
    suspend fun saveDailyProgress(
        userId: Long,
        planId: String,
        date: LocalDate,
        completedTasks: List<String>,
        nutritionData: Map<String, Double> = emptyMap(),
        notes: String? = null
    )
    suspend fun getDailyProgress(
        userId: Long,
        planId: String,
        date: LocalDate
    ): DailyProgress?
    fun getPlanProgressHistory(userId: Long, planId: String): Flow<List<DailyProgress>>
    suspend fun getUserPlanStatistics(userId: Long): PlanStatistics
    suspend fun updatePlanStatus(planId: String, status: String)
}