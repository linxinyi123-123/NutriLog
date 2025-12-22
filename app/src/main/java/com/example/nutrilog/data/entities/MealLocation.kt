package com.example.nutrilog.data.entities

enum class MealLocation(
    val displayName: String,
    val icon: String
) {
    HOME("家里", "ic_home"),
    CAFETERIA("食堂", "ic_cafeteria"),
    RESTAURANT("餐厅", "ic_restaurant"),
    TAKEAWAY("外卖", "ic_takeaway"),
    OFFICE("办公室", "ic_office"),
    OUTDOOR("户外", "ic_outdoor"),
    OTHER("其他", "ic_other");
}