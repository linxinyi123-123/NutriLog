package com.example.nutrilog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class FoodItemWithAmount(
    @Embedded
    val food: FoodItem,
    
    @ColumnInfo(name = "amount")
    val amount: Double,
    
    @ColumnInfo(name = "unit")
    val unit: FoodUnit,
    
    @ColumnInfo(name = "note")
    val note: String
)