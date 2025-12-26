package com.example.nutrilog.features.recommendation.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recommendation_rules")
data class RecommendationRuleEntity(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "condition")
    val condition: String,

    @ColumnInfo(name = "action")
    val action: String,

    @ColumnInfo(name = "priority")
    val priority: String,

    @ColumnInfo(name = "message")
    val message: String
)