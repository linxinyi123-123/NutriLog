package com.example.nutrilog.data.converters

import androidx.room.TypeConverter
import com.example.nutrilog.data.entities.FoodCategory

class FoodCategoryConverter {
    @TypeConverter
    fun fromString(value: String?): FoodCategory? {
        return value?.let { FoodCategory.valueOf(it) }
    }
    
    @TypeConverter
    fun toString(category: FoodCategory?): String? {
        return category?.name
    }
}