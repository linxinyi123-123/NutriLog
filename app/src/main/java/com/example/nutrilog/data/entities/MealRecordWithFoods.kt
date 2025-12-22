package com.nutrilog.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class MealRecordWithFoods(
    @Embedded
    val record: MealRecord,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "record_id",
        entity = RecordFoodItem::class
    )
    val foodItems: List<RecordFoodItem>
)