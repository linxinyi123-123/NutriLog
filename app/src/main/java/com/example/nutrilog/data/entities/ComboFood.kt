package com.example.nutrilog.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = FoodCombo::class,
        parentColumns = ["id"],
        childColumns = ["comboId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [androidx.room.Index(value = ["comboId"])]
)
data class ComboFood(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val comboId: Long = 0,
    val foodId: Long = 0,
    val foodName: String = "",
    val portion: Double = 0.0,
    val unit: String = ""            
)