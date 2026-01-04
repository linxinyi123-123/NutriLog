package com.example.nutrilog.features.recommendation.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.nutrilog.features.recommendation.database.converter.DateConverter
import com.example.nutrilog.features.recommendation.database.converter.ImprovementPlanConverter
import java.time.LocalDate

@Entity(tableName = "improvement_plans")
@TypeConverters(DateConverter::class, ImprovementPlanConverter::class)
data class ImprovementPlanEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "goal_type")
    val goalType: String,  // GoalType的name

    @ColumnInfo(name = "health_goal_id")
    val healthGoalId: Long? = null,

    @ColumnInfo(name = "duration")
    val duration: Int,

    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,

    @ColumnInfo(name = "end_date")
    val endDate: LocalDate,

    @ColumnInfo(name = "current_week")
    val currentWeek: Int = 1,

    @ColumnInfo(name = "total_weeks")
    val totalWeeks: Int,

    @ColumnInfo(name = "weekly_plans_json")
    val weeklyPlansJson: String, // WeeklyPlan列表的JSON

    @ColumnInfo(name = "daily_templates_json")
    val dailyTemplatesJson: String = "[]", // DailyTask列表的JSON

    @ColumnInfo(name = "status")
    val status: String = "ACTIVE", // PlanStatus的name

    @ColumnInfo(name = "progress")
    val progress: Float = 0f,

    @ColumnInfo(name = "completed_weeks_json")
    val completedWeeksJson: String = "[]", // Set<Int>的JSON

    @ColumnInfo(name = "milestones_json")
    val milestonesJson: String = "[]", // Milestone列表的JSON

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "notes")
    val notes: String? = null
)