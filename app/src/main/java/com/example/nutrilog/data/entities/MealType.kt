package com.example.nutrilog.data.entities

enum class MealType(
    val displayName: String,
    val icon: String,
    val startTime: String,
    val endTime: String
) {
    BREAKFAST("早餐", "ic_breakfast", "06:00", "10:00"),
    LUNCH("午餐", "ic_lunch", "11:00", "14:00"),
    DINNER("晚餐", "ic_dinner", "17:00", "20:00"),
    SNACK("零食", "ic_snack", "00:00", "23:59"),
    SUPPER("夜宵", "ic_supper", "20:00", "02:00");
    
    companion object {
        fun fromTime(time: String): MealType {
            val hour = time.substring(0, 2).toInt()
            
            return when (hour) {
                in 6..10 -> BREAKFAST
                in 11..14 -> LUNCH
                in 17..20 -> DINNER
                in 20..23, in 0..2 -> SUPPER
                else -> SNACK
            }
        }
    }
}