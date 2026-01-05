// app/src/main/java/com/nutrilog/features/recommendation/interfaces/NutritionProvider.kt  
package com.example.nutrilog.features.recommendation.interfaces

/**
 * 营养数据提供接口（D10将由B同学实现）
 */
interface NutritionProvider {
    suspend fun getNutritionalGaps(userId: Long, days: Int): List<NutritionalGap>
    suspend fun getNutrientAverage(userId: Long, nutrient: String, days: Int): Double
}