package com.nutrilog.data.converters

import androidx.room.TypeConverter
import com.nutrilog.data.entities.FoodUnit

class FoodUnitConverter {
    @TypeConverter
    fun fromString(value: String?): FoodUnit? {
        return value?.let { FoodUnit.valueOf(it) }
    }
    
    @TypeConverter
    fun toString(unit: FoodUnit?): String? {
        return unit?.name
    }
}