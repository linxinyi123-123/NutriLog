package com.example.nutrilog.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FoodCombo(
    @PrimaryKey(autoGenerate = true)  // 自动生成ID
    val id: Long = 0,
    val name: String= "",                  // 组合名称（如"早餐套餐"）
    val description: String = ""       // 描述（可选）
)