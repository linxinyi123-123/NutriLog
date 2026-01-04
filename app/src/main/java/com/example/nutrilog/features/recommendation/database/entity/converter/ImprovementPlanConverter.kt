// 修改后的ImprovementPlanConverter.kt
package com.example.nutrilog.features.recommendation.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ImprovementPlanConverter {
    private val gson = Gson()

    // WeeklyPlan列表
    @TypeConverter
    fun weeklyPlansToJson(weeklyPlans: List<Any>?): String {
        return gson.toJson(weeklyPlans ?: emptyList<Any>())
    }

    @TypeConverter
    fun jsonToWeeklyPlans(json: String?): List<Any> {
        if (json.isNullOrEmpty()) return emptyList()
        val type: Type = object : TypeToken<List<Any>>() {}.type
        return gson.fromJson(json, type)
    }

    // DailyTask列表
    @TypeConverter
    fun dailyTasksToJson(dailyTasks: List<Any>?): String {
        return gson.toJson(dailyTasks ?: emptyList<Any>())
    }

    @TypeConverter
    fun jsonToDailyTasks(json: String?): List<Any> {
        if (json.isNullOrEmpty()) return emptyList()
        val type: Type = object : TypeToken<List<Any>>() {}.type
        return gson.fromJson(json, type)
    }

    // Milestone列表
    @TypeConverter
    fun milestonesToJson(milestones: List<Any>?): String {
        return gson.toJson(milestones ?: emptyList<Any>())
    }

    @TypeConverter
    fun jsonToMilestones(json: String?): List<Any> {
        if (json.isNullOrEmpty()) return emptyList()
        val type: Type = object : TypeToken<List<Any>>() {}.type
        return gson.fromJson(json, type)
    }

    // Set<Int>（已完成的周数）
    @TypeConverter
    fun completedWeeksToJson(weeks: Set<Int>?): String {
        return gson.toJson(weeks ?: emptySet<Int>())
    }

    @TypeConverter
    fun jsonToCompletedWeeks(json: String?): Set<Int> {
        if (json.isNullOrEmpty()) return emptySet()
        val type: Type = object : TypeToken<Set<Int>>() {}.type
        return gson.fromJson(json, type)
    }

    // Map<String, Double>（营养数据）
    @TypeConverter
    fun nutritionDataToJson(data: Map<String, Double>?): String {
        return gson.toJson(data ?: emptyMap<String, Double>())
    }

    @TypeConverter
    fun jsonToNutritionData(json: String?): Map<String, Double> {
        if (json.isNullOrEmpty()) return emptyMap()
        val type: Type = object : TypeToken<Map<String, Double>>() {}.type
        return gson.fromJson(json, type)
    }
}