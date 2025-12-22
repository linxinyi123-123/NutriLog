package com.example.nutrilog.data.converters

import androidx.room.TypeConverter
import com.example.nutrilog.data.entities.MealLocation

class MealLocationConverter {
    @TypeConverter
    fun fromString(value: String?): MealLocation? {
        return value?.let { MealLocation.valueOf(it) }
    }
    
    @TypeConverter
    fun toString(location: MealLocation?): String? {
        return location?.name
    }
}