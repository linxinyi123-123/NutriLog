package com.example.nutrilog.features.recommendation.model

/**
 * 地点类型枚举
 */
enum class LocationType {
    CAFETERIA,      // 食堂
    DELIVERY,       // 外卖
    RESTAURANT,     // 餐厅
    HOME_COOKING,   // 自制
    FAST_FOOD,      // 快餐店
    CAFE,           // 咖啡馆
    CONVENIENCE_STORE, // 便利店
    SUPERMARKET,    // 超市
    OFFICE,         // 办公室
    OTHER           // 其他
}

/**
 * 地点相关信息
 */
data class LocationInfo(
    val type: LocationType,
    val name: String? = null,
    val budgetLevel: BudgetLevel = BudgetLevel.MEDIUM,
    val healthiness: Int = 50, // 0-100
    val availableOptions: List<String> = emptyList()
)

/**
 * 预算等级
 */
enum class BudgetLevel {
    LOW,        // 低价 (<20元)
    MEDIUM,     // 中等 (20-50元)
    HIGH,       // 高价 (>50元)
    PREMIUM     // 高级 (>100元)
}

/**
 * 地点推荐相关工具
 */
object LocationRecommenderHelper {

    /**
     * 根据地点类型获取推荐的食物类别
     */
    fun getRecommendedFoodCategories(location: LocationType): List<String> {
        return when (location) {
            LocationType.CAFETERIA -> listOf("套餐", "主食", "蔬菜", "蛋白质")
            LocationType.DELIVERY -> listOf("中式", "西式", "轻食", "汤品")
            LocationType.RESTAURANT -> listOf("特色菜", "主菜", "配菜", "甜品")
            LocationType.HOME_COOKING -> listOf("家常菜", "健康餐", "创意菜")
            LocationType.FAST_FOOD -> listOf("汉堡", "炸鸡", "薯条", "饮料")
            LocationType.CAFE -> listOf("咖啡", "三明治", "甜点", "沙拉")
            LocationType.CONVENIENCE_STORE -> listOf("便当", "饭团", "关东煮", "饮料")
            LocationType.SUPERMARKET -> listOf("生鲜", "半成品", "零食", "饮品")
            LocationType.OFFICE -> listOf("便当", "沙拉", "三明治", "水果")
            LocationType.OTHER -> listOf("通用")
        }
    }

    /**
     * 获取地点的健康程度评分
     */
    fun getHealthinessScore(location: LocationType): Int {
        return when (location) {
            LocationType.HOME_COOKING -> 90
            LocationType.CAFETERIA -> 70
            LocationType.SUPERMARKET -> 65
            LocationType.RESTAURANT -> 60
            LocationType.CAFE -> 55
            LocationType.OFFICE -> 50
            LocationType.DELIVERY -> 45
            LocationType.CONVENIENCE_STORE -> 40
            LocationType.FAST_FOOD -> 30
            LocationType.OTHER -> 50
        }
    }

    /**
     * 获取地点的平均预算
     */
    fun getAverageBudget(location: LocationType): BudgetLevel {
        return when (location) {
            LocationType.HOME_COOKING -> BudgetLevel.LOW
            LocationType.CAFETERIA -> BudgetLevel.LOW
            LocationType.CONVENIENCE_STORE -> BudgetLevel.LOW
            LocationType.FAST_FOOD -> BudgetLevel.MEDIUM
            LocationType.OFFICE -> BudgetLevel.MEDIUM
            LocationType.DELIVERY -> BudgetLevel.MEDIUM
            LocationType.SUPERMARKET -> BudgetLevel.MEDIUM
            LocationType.CAFE -> BudgetLevel.MEDIUM
            LocationType.RESTAURANT -> BudgetLevel.HIGH
            LocationType.OTHER -> BudgetLevel.MEDIUM
        }
    }
}