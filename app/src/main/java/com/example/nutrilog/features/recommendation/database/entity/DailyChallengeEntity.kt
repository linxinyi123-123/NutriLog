// app/src/main/java/com/nutrilog/features/recommendation/database/entity/DailyChallengeEntity.kt
package com.example.nutrilog.features.recommendation.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "date")
    val date: String,           // 格式：2024-01-15

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "type")
    val type: String,           // ChallengeType的字符串表示

    @ColumnInfo(name = "reward_points")
    val rewardPoints: Int,

    @ColumnInfo(name = "difficulty")
    val difficulty: String,     // ChallengeDifficulty的字符串表示

    @ColumnInfo(name = "progress")
    val progress: Float = 0f,

    @ColumnInfo(name = "target")
    val target: Float,

    @ColumnInfo(name = "unit")
    val unit: String,

    @ColumnInfo(name = "completed")
    val completed: Boolean = false,

    @ColumnInfo(name = "metadata")
    val metadata: String = "",  // JSON字符串存储扩展数据

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)