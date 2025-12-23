package com.example.nutrilog.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "record_food_items",
    primaryKeys = ["record_id", "food_id"],
    foreignKeys = [
        ForeignKey(
            entity = MealRecord::class,
            parentColumns = ["id"],
            childColumns = ["record_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodItem::class,
            parentColumns = ["id"],
            childColumns = ["food_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["record_id"]),
        Index(value = ["food_id"])
    ]
)
data class RecordFoodItem(
    @ColumnInfo(name = "record_id")
    val recordId: Long,
    
    @ColumnInfo(name = "food_id")
    val foodId: Long,
    
    @ColumnInfo(name = "amount")
    val amount: Double,           // 数量
    
    @ColumnInfo(name = "unit")
    val unit: FoodUnit,           // 单位
    
    @ColumnInfo(name = "custom_unit")
    val customUnit: String? = null,  // 自定义单位描述
    
    @ColumnInfo(name = "preparation")
    val preparation: String? = null, // 烹饪方式，如"蒸","炒"
    
    @ColumnInfo(name = "note")
    val note: String = "",        // 该食物的备注
    
    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0       // 显示顺序
)
