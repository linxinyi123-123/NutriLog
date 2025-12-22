package com.example.nutrilog.common.models

data class UserProfile(
    val id: Long = 1,
    val name: String = "用户",
    val age: Int = 20,
    val gender: Gender = Gender.MALE,
    val height: Int = 170, // cm
    val weight: Int = 65,  // kg
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,

)

enum class Gender { MALE, FEMALE }
enum class ActivityLevel { SEDENTARY, LIGHT, MODERATE, ACTIVE, VERY_ACTIVE }