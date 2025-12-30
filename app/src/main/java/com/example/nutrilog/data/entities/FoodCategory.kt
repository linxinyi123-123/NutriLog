package com.example.nutrilog.data.entities

enum class FoodCategory(
    val displayName: String,
    val icon: String,
    val color: String
) {
    GRAINS("主食", "ic_grains", "#8BC34A"),
    VEGETABLES("蔬菜", "ic_vegetables", "#4CAF50"),
    FRUITS("水果", "ic_fruits", "#FF9800"),
    PROTEIN("蛋白质", "ic_protein", "#F44336"),
    DAIRY("奶制品", "ic_dairy", "#2196F3"),
    NUTS("坚果", "ic_nuts", "#795548"),
    OILS("油脂", "ic_oils", "#FFC107"),
    SNACKS("零食", "ic_snacks", "#9E9E9E"),
    BEVERAGES("饮料", "ic_beverages", "#03A9F4"),
    SEASONINGS("调味品", "ic_seasonings", "#607D8B"),
    OTHERS("其他", "ic_others", "#9C27B0");
    
    companion object {
        fun fromString(value: String): FoodCategory {
            return values().find { it.displayName == value } ?: OTHERS
        }

    }


}