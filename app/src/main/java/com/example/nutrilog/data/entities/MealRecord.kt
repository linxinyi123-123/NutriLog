package com.nutrilog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_records",
    indices = [
        Index(value = ["date", "user_id"]),
        Index(value = ["meal_type"]),
        Index(value = ["created_at"])
    ]
)
data class MealRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "user_id")
    val userId: Long = 1,  // 单用户应用，默认为1
    
    @ColumnInfo(name = "date")
    val date: String,      // 格式: "2024-01-15"
    
    @ColumnInfo(name = "time")
    val time: String,      // 格式: "08:30"
    
    @ColumnInfo(name = "meal_type")
    val mealType: MealType,
    
    @ColumnInfo(name = "location")
    val location: MealLocation,
    
    @ColumnInfo(name = "mood")
    val mood: Int = 3,     // 1-5，默认3
    
    @ColumnInfo(name = "note")
    val note: String = "",
    
    @ColumnInfo(name = "photo_path")
    val photoPath: String? = null,  // 模拟图片路径
    
    @ColumnInfo(name = "tags")
    val tags: String? = null,  // 逗号分隔的标签
    
    @ColumnInfo(name = "is_quick_add")
    val isQuickAdd: Boolean = false,  // 是否为快速添加
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    // 获取显示时间
    fun getDisplayTime(): String {
        return "$date $time"
    }
}