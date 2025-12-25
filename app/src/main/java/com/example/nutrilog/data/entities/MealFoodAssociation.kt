package com.example.nutrilog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_food_associations",
    foreignKeys = [
        ForeignKey(
            entity = MealRecord::class,
            parentColumns = ["id"],
            childColumns = ["meal_record_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodItem::class,
            parentColumns = ["id"],
            childColumns = ["food_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["meal_record_id"]),
        Index(value = ["food_id"]),
        Index(value = ["meal_record_id", "food_id"], unique = true)
    ]
)
data class MealFoodAssociation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "meal_record_id")
    val mealRecordId: Long,
    
    @ColumnInfo(name = "food_id")
    val foodId: Long,
    
    @ColumnInfo(name = "quantity")
    val quantity: Double,  // 份量（克）
    
    @ColumnInfo(name = "unit")
    val unit: String = "g"  // 单位，默认为克
)