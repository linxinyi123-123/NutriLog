package com.example.nutrilog.features.recommendation.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.nutrilog.features.recommendation.database.converter.DateConverter
import java.time.LocalDate

@Entity(tableName = "daily_progress")
@TypeConverters(DateConverter::class)
data class DailyProgressEntity(
    @PrimaryKey
    val id: String, // 格式: "userId_planId_date"

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "plan_id")
    val planId: String,

    @ColumnInfo(name = "date")
    val date: LocalDate,

    @ColumnInfo(name = "completed_tasks_json")
    val completedTasksJson: String = "[]", // List<String>的JSON

    @ColumnInfo(name = "nutrition_data_json")
    val nutritionDataJson: String = "{}", // Map<String, Double>的JSON

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)