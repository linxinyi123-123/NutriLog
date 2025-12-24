package com.nutrilog.features.recommendation.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "icon")
    val icon: String,

    @ColumnInfo(name = "points")
    val points: Int,

    @ColumnInfo(name = "is_unlocked")
    val isUnlocked: Boolean = false,

    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Long? = null,

    @ColumnInfo(name = "progress")
    val progress: Float = 0f
)