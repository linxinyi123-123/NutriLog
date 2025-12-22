package com.example.nutrilog.data.converters

import androidx.room.TypeConverter
import com.example.nutrilog.data.entities.MealType

class MealTypeConverter {
    @TypeConverter
    fun fromString(value: String?): MealType? {
        return value?.let { MealType.valueOf(it) }
    }
    
    @TypeConverter
    fun toString(mealType: MealType?): String? {
        return mealType?.name
    }
}