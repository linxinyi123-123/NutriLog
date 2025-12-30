package com.example.nutrilog.shared

//用于计算
enum class FoodCategory(
    val chineseName: String,
    val recommendedDailyServings: IntRange
) {
    GRAINS("谷薯类", 3..6),          // 米饭、面条、面包、土豆
    VEGETABLES("蔬菜类", 4..5),      // 叶菜、根茎、瓜果
    FRUITS("水果类", 2..4),          // 各种水果
    PROTEIN("蛋白质类", 3..5),       // 肉、鱼、蛋、豆制品
    DAIRY("奶制品", 1..2),           // 牛奶、酸奶、奶酪
    NUTS("坚果类", 0..1),      // 坚果、种子
    OILS("油脂类", 2..3),            // 食用油、脂肪
    SNACKS("零食", 0..1),
    BEVERAGES("饮料",1..8 ),
    SEASONINGS("调味品",0..1 ),
    OTHERS("其他", 0..1);

    companion object {
        fun fromFoodName(foodName: String): FoodCategory {
            // 根据食物名称判断类别
            return when {
                foodName.contains("饭") || foodName.contains("面") -> GRAINS
                foodName.contains("菜") || foodName.contains("蔬") -> VEGETABLES
                foodName.contains("肉") || foodName.contains("鱼") -> PROTEIN
                else -> GRAINS // 默认
            }
        }
    }
}