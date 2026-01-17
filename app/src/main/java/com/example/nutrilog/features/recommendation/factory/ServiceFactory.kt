// features/recommendation/factory/ServiceFactory.kt
package com.example.nutrilog.features.recommendation.factory

import com.example.nutrilog.features.recommendation.adapters.*
import com.example.nutrilog.features.recommendation.algorithm.*
import com.example.nutrilog.features.recommendation.mock.*
import com.example.nutrilog.features.recommendation.service.RecommendationService
import com.example.nutrilog.features.recommendation.service.RecommendationServiceImpl
import com.example.nutrilog.features.recommendation.repository.ImprovementPlanRepository
import com.example.nutrilog.features.recommendation.repository.MockImprovementPlanRepository

object ServiceFactory {

    enum class DataSource {
        MOCK, REAL
    }

    fun createRecommendationService(dataSource: DataSource = DataSource.MOCK): RecommendationService {
        return when (dataSource) {
            DataSource.MOCK -> createMockService()
            DataSource.REAL -> createRealService()
        }
    }

    private fun createMockService(): RecommendationService {
        // 1. 创建Mock数据提供者
        val mockRecordProvider = MockRecordProvider()
        val mockNutritionProvider = MockNutritionProvider()

        // 2. 创建适配器
        val recordRepository = RecordProviderAdapter(mockRecordProvider)
        val nutritionAnalysisService = NutritionProviderAdapter(mockNutritionProvider)
        val goalRepository = MockGoalAdapter()

        // 3. 创建Mock推荐仓库（需要实现）
        val recommendationRepository = MockRecommendationRepository()

        // 4. 创建Mock改善计划仓库
        val planRepository: ImprovementPlanRepository = MockImprovementPlanRepository()

        // 5. 创建各种推荐器
        val gapRecommender = NutritionalGapRecommender()
        val goalRecommender = GoalBasedRecommender()
        val contextRecommender = ContextAwareRecommender()
        val timeRecommender = TimeBasedRecommender()
        val locationRecommender = LocationBasedRecommender()

        // 6. 创建改善计划生成器和跟踪器
        val planGenerator = ImprovementPlanGenerator()

        // 只需要传入 planRepository，第二个参数使用默认值
        val planTracker = PlanTracker(planRepository)

        return RecommendationServiceImpl(
            recordRepository = recordRepository,
            nutritionAnalysisService = nutritionAnalysisService,
            goalRepository = goalRepository,
            recommendationRepository = recommendationRepository,
            planRepository = planRepository,
            gapRecommender = gapRecommender,
            goalRecommender = goalRecommender,
            contextRecommender = contextRecommender,
            timeRecommender = timeRecommender,
            locationRecommender = locationRecommender,
            planGenerator = planGenerator,
            planTracker = planTracker
        )
    }

    private fun createRealService(): RecommendationService {
        // TODO: 实际实现需要传入真实的 ImprovementPlanDao
        // val improvementPlanDao = // 获取实际的 Dao
        // val planRepository: ImprovementPlanRepository = ImprovementPlanRepositoryImpl(improvementPlanDao)
        return createMockService() // 暂时返回Mock服务
    }
}