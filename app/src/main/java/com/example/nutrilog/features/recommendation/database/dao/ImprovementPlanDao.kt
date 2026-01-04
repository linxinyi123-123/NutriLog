package com.example.nutrilog.features.recommendation.database.dao

import androidx.room.*
import com.example.nutrilog.features.recommendation.database.entity.DailyProgressEntity
import com.example.nutrilog.features.recommendation.database.entity.ImprovementPlanEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ImprovementPlanDao {

    // ImprovementPlan操作
    @Query("SELECT * FROM improvement_plans WHERE user_id = :userId ORDER BY created_at DESC")
    fun getUserPlans(userId: Long): Flow<List<ImprovementPlanEntity>>

    @Query("SELECT * FROM improvement_plans WHERE user_id = :userId AND status = 'ACTIVE'")
    fun getActivePlans(userId: Long): Flow<List<ImprovementPlanEntity>>

    @Query("SELECT * FROM improvement_plans WHERE id = :planId AND user_id = :userId")
    suspend fun getPlan(userId: Long, planId: String): ImprovementPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: ImprovementPlanEntity)

    @Update
    suspend fun updatePlan(plan: ImprovementPlanEntity)

    @Query("UPDATE improvement_plans SET status = :status WHERE id = :planId")
    suspend fun updatePlanStatus(planId: String, status: String)

    @Query("UPDATE improvement_plans SET progress = :progress WHERE id = :planId")
    suspend fun updatePlanProgress(planId: String, progress: Float)

    @Query("UPDATE improvement_plans SET current_week = :week WHERE id = :planId")
    suspend fun updateCurrentWeek(planId: String, week: Int)

    @Query("UPDATE improvement_plans SET completed_weeks_json = :completedWeeks WHERE id = :planId")
    suspend fun updateCompletedWeeks(planId: String, completedWeeks: String)

    @Query("DELETE FROM improvement_plans WHERE id = :planId")
    suspend fun deletePlan(planId: String)

    // DailyProgress操作
    @Query("SELECT * FROM daily_progress WHERE user_id = :userId AND plan_id = :planId AND date = :date")
    suspend fun getDailyProgress(userId: Long, planId: String, date: LocalDate): DailyProgressEntity?

    @Query("SELECT * FROM daily_progress WHERE user_id = :userId AND plan_id = :planId ORDER BY date DESC")
    fun getPlanProgressHistory(userId: Long, planId: String): Flow<List<DailyProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyProgress(progress: DailyProgressEntity)

    @Update
    suspend fun updateDailyProgress(progress: DailyProgressEntity)

    @Query("DELETE FROM daily_progress WHERE plan_id = :planId")
    suspend fun deletePlanProgress(planId: String)
}