package com.example.nutrilog.shared

data class UserProfile(
    val gender: String,       // male/female
    val age: Int,
    val height: Double,       // 厘米
    val weight: Double,       // 公斤
    val activityLevel: String // sedentary/light/moderate/active
)

