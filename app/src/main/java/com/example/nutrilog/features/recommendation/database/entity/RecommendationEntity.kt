package com.nutrilog.features.recommendation.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "recommendations")
data class RecommendationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "type")
    val type: String,  // 存储RecommendationType的name

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "priority")
    val priority: String,  // 存储Priority的name

    @ColumnInfo(name = "confidence")
    val confidence: Float,

    @ColumnInfo(name = "reason")
    val reason: String,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "is_applied")
    val isApplied: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long? = null
)