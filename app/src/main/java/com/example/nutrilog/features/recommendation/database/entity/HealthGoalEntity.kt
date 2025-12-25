package com.example.nutrilog.features.recommendation.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_goals")
data class HealthGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "type")
    val type: String,  // 存储GoalType的name

    @ColumnInfo(name = "target_value")
    val targetValue: Double,

    @ColumnInfo(name = "target_unit")
    val targetUnit: String,

    @ColumnInfo(name = "current_value")
    val currentValue: Double = 0.0,

    @ColumnInfo(name = "progress")
    val progress: Float = 0f,

    @ColumnInfo(name = "start_date")
    val startDate: String,

    @ColumnInfo(name = "end_date")
    val endDate: String,

    @ColumnInfo(name = "status")
    val status: String = "ACTIVE",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)