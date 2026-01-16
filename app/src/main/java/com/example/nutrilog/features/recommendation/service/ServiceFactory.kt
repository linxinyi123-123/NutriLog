package com.example.nutrilog.features.recommendation.service

import com.example.nutrilog.features.recommendation.mock.MockNutritionProvider
import com.example.nutrilog.features.recommendation.mock.MockRecordProvider

// features/recommendation/service/ServiceFactory.kt
object ServiceFactory {
    // 配置：true使用模拟数据，false使用真实数据
    private var useMockData = true  // 开发阶段先用模拟

    fun createRecommendationService(
        aService: A_RecordService? = null,
        bService: B_NutritionService? = null
    ): RecommendationService {
        val recordProvider = if (useMockData) {
            MockRecordProvider()
        } else {
            // 将来换成适配器
            // AdaptedRecordProvider(aService!!)
            // 现在先用模拟
            MockRecordProvider()
        }

        val nutritionProvider = if (useMockData) {
            MockNutritionProvider()
        } else {
            // AdaptedNutritionProvider(bService!!)
            MockNutritionProvider()
        }

        return RecommendationServiceImpl(recordProvider, nutritionProvider)
    }

    // 演示日调用这个方法切换到真实数据
    fun switchToRealData(enabled: Boolean) {
        useMockData = !enabled
    }
}