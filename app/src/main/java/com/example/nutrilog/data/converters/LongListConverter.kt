package com.example.nutrilog.data.converters

import androidx.room.TypeConverter

class LongListConverter {
    @TypeConverter
    fun fromString(value: String?): List<Long>? {
        return value?.split(",")?.map { it.toLong() }
    }
    
    @TypeConverter
    fun toString(list: List<Long>?): String? {
        return list?.joinToString(",")
    }
}