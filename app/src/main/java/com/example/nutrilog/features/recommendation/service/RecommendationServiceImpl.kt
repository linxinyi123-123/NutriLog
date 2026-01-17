
// features/recommendation/service/RecommendationServiceImpl.kt
package com.example.nutrilog.features.recommendation.service

import com.example.nutrilog.data.entities.MealType
import com.example.nutrilog.features.recommendation.model.*
import com.example.nutrilog.features.recommendation.model.gamification.Achievement
import com.example.nutrilog.features.recommendation.model.gamification.AchievementType
import com.example.nutrilog.features.recommendation.model.gamification.Condition
import com.example.nutrilog.features.recommendation.challenge.DailyChallenge
import com.example.nutrilog.features.recommendation.challenge.ChallengeType
import com.example.nutrilog.features.recommendation.challenge.ChallengeDifficulty
import com.example.nutrilog.features.recommendation.algorithm.*
import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.interfaces.*
import com.example.nutrilog.features.recommendation.model.improvement.*
import com.example.nutrilog.features.recommendation.repository.ImprovementPlanRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecommendationServiceImpl (
    private val recordRepository: RecordRepository,
    private val nutritionAnalysisService: NutritionAnalysisService,
    private val goalRepository: GoalRepository,
    private val recommendationRepository: RecommendationRepository,
    private val planRepository: ImprovementPlanRepository,

    // 各种推荐器
    private val gapRecommender: NutritionalGapRecommender,
    private val goalRecommender: GoalBasedRecommender,
    private val contextRecommender: ContextAwareRecommender,
    private val timeRecommender: TimeBasedRecommender,
    private val locationRecommender: LocationBasedRecommender,
    private val planGenerator: ImprovementPlanGenerator,
    private val planTracker: PlanTracker
) : RecommendationService {  // 添加接口实现

    override suspend fun getDailyRecommendations(userId: Long): List<Recommendation> {
        // 1. 构建推荐上下文
        val context = buildRecommendationContext(userId)
        
        // 2. 生成所有推荐
        val recommendations = mutableListOf<Recommendation>()
        
        // 新用户引导性推荐
        if (context.isFirstTimeUser) {
            val newUserRecommendation = Recommendation(
                id = System.currentTimeMillis(),
                type = RecommendationType.MEAL_PLAN,
                title = "记录你的第一餐",
                description = "欢迎使用NutriLog！开始记录你的第一餐，让我们一起关注你的健康饮食。",
                priority = Priority.HIGH,
                confidence = 1.0f,
                reason = "新用户引导",
                actions = listOf(
                    Action.AddToMealPlan(emptyList()),
                    Action.DismissRecommendation("稍后提醒")
                ),
                metadata = mapOf(
                    "isNewUser" to true,
                    "guidanceType" to "first_meal"
                )
            )
            recommendations.add(newUserRecommendation)
            recommendationRepository.saveRecommendation(newUserRecommendation)
        }
        
        // 其他推荐
        val gapRecs = gapRecommender.generateRecommendations(context.nutritionalGaps, context)
        val goalRecs = goalRecommender.generateGoalRecommendations(context.healthGoals, context)
        val contextRecs = contextRecommender.generateContextRecommendations(context)
        val timeRecs = timeRecommender.generateTimeRecommendations(context)
        val locationRecs = locationRecommender.generateLocationRecommendations(context)
        
        recommendations.addAll(gapRecs)
        recommendations.addAll(goalRecs)
        recommendations.addAll(contextRecs)
        recommendations.addAll(timeRecs)
        recommendations.addAll(locationRecs)
        
        // 保存所有推荐到仓库
        recommendations.forEach { recommendationRepository.saveRecommendation(it) }

        // 3. 过滤和排序
        return filterAndRankRecommendations(recommendations, userId)
    }

    private suspend fun buildRecommendationContext(userId: Long): RecommendationContext {
        // 获取营养分析数据
        val nutritionalGaps = nutritionAnalysisService.getNutritionalGaps(userId, 7)
        val mealPatterns = nutritionAnalysisService.getEatingPatterns(userId)
        val healthScore = nutritionAnalysisService.getLatestHealthScore(userId)

        // 获取饮食记录
        val recentMeals = recordRepository.getUserRecords(userId, 7)

        // 获取目标
        val healthGoals = goalRepository.getActiveGoals(userId)

        // 获取当前时间
        val currentTime = System.currentTimeMillis()

        return RecommendationContext(
            userId = userId,
            currentTime = currentTime,
            currentHour = getCurrentHour(currentTime),
            nutritionalGaps = nutritionalGaps,
            mealPatterns = mealPatterns,
            healthScore = healthScore.toInt(),
            recentMeals = recentMeals,
            healthGoals = healthGoals,
            mealType = determineMealType(getCurrentHour(currentTime)),
            isFirstTimeUser = isFirstTimeUser(userId)
        )
    }

    private fun getCurrentHour(currentTime: Long): Int {
        // 简化的时间获取，实际应该使用Calendar或LocalDateTime
        return ((currentTime / (1000 * 60 * 60)) % 24).toInt()
    }

    private fun determineMealType(currentHour: Int): String {
        return when (currentHour) {
            in 5..9 -> "早餐"
            in 10..13 -> "午餐"
            in 16..20 -> "晚餐"
            else -> "加餐"
        }
    }

    private suspend fun isFirstTimeUser(userId: Long): Boolean {
        // 检查是否为首次使用（例如，记录少于3次）
        val recordCount = recordRepository.getUserRecordCount(userId)
        return recordCount < 3
    }

    private suspend fun filterAndRankRecommendations(
        recommendations: List<Recommendation>,
        userId: Long
    ): List<Recommendation> {
        // 获取已处理的推荐
        val processedIds = recommendationRepository.getProcessedRecommendations(userId)

        return recommendations
            .filterNot { it.id in processedIds }  // 过滤已读
            .distinctBy { "${it.type}:${it.title}:${it.reason}" }  // 去重
            .sortedWith(
                compareByDescending<Recommendation> { it.priority }
                    .thenByDescending { it.confidence }
            )  // 按优先级和置信度排序
            .take(10)  // 限制数量
    }

    override suspend fun getContextRecommendations(
        userId: Long,
        context: RecommendationContext
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()
        recommendations.addAll(gapRecommender.generateRecommendations(context.nutritionalGaps, context))
        recommendations.addAll(goalRecommender.generateGoalRecommendations(context.healthGoals, context))
        recommendations.addAll(contextRecommender.generateContextRecommendations(context))

        return recommendations
            .sortedByDescending { it.priority }
            .take(5)
    }

    override suspend fun getImprovementPlan(
        userId: Long,
        goalType: GoalType
    ): ImprovementPlan? {
        val context = buildRecommendationContext(userId)
        val goal = context.healthGoals.firstOrNull { it.type == goalType }

        return goal?.let {
            // 使用新的ImprovementPlanGenerator生成计划
            planGenerator.generatePlanForGoal(goal, context, 4)
        }
    }

    override suspend fun getDailyChallenges(userId: Long): List<DailyChallenge> {
        val today = LocalDate.now()
        val streakDays = recordRepository.getStreakDays(userId)
        val foodVarietyCount = recordRepository.getFoodVarietyCount(userId, 1) // 今日食物种类数

        // 检查今天是否已经完成过挑战
        val completedChallenges = recommendationRepository.getTodaysCompletedChallenges(userId, today)

        // 获取今日已完成的挑战ID列表
        val completedIds = completedChallenges.map { it.id }

        // 计算今日已记录餐数
        val todayRecords = recordRepository.getTodayRecords(userId)
        val mealCount = todayRecords.size
        val breakfastCount = todayRecords.count { it.mealType == MealType.BREAKFAST }
        val lunchCount = todayRecords.count { it.mealType == MealType.LUNCH }
        val dinnerCount = todayRecords.count { it.mealType == MealType.DINNER }

        val allChallenges = listOf(
            DailyChallenge(
                id = 1,
                userId = userId,
                date = today.format(DateTimeFormatter.ISO_LOCAL_DATE),
                title = "记录完整三餐",
                description = "今天记录早餐、午餐、晚餐各一次",
                type = ChallengeType.MEAL_RECORD,
                rewardPoints = 10,
                difficulty = ChallengeDifficulty.EASY,
                progress = minOf(3f, breakfastCount + lunchCount + dinnerCount.toFloat()),
                target = 3f,
                unit = "餐",
                completed = 1L in completedIds,
                metadata = mapOf(
                    "breakfastCount" to breakfastCount,
                    "lunchCount" to lunchCount,
                    "dinnerCount" to dinnerCount
                )
            ),
            DailyChallenge(
                id = 2,
                userId = userId,
                date = today.format(DateTimeFormatter.ISO_LOCAL_DATE),
                title = "摄入5种不同食物",
                description = "今天尝试吃5种不同的食物",
                type = ChallengeType.FOOD_VARIETY,
                rewardPoints = 15,
                difficulty = ChallengeDifficulty.MEDIUM,
                progress = minOf(5f, foodVarietyCount.toFloat()),
                target = 5f,
                unit = "种",
                completed = 2L in completedIds,
                metadata = mapOf("currentVariety" to foodVarietyCount)
            ),
            DailyChallenge(
                id = 3,
                userId = userId,
                date = today.format(DateTimeFormatter.ISO_LOCAL_DATE),
                title = "喝足够的水",
                description = "今天喝至少8杯水",
                type = ChallengeType.HYDRATION,
                rewardPoints = 8,
                difficulty = ChallengeDifficulty.EASY,
                progress = 0f, // 需要从数据库获取实际水摄入量
                target = 8f,
                unit = "杯",
                completed = 3L in completedIds,
                metadata = mapOf("waterIntake" to 0)
            ),
            DailyChallenge(
                id = 4,
                userId = userId,
                date = today.format(DateTimeFormatter.ISO_LOCAL_DATE),
                title = "保持连续记录",
                description = "连续记录${streakDays + 1}天饮食",
                type = ChallengeType.STREAK,
                rewardPoints = 20,
                difficulty = ChallengeDifficulty.HARD,
                progress = streakDays.toFloat(),
                target = (streakDays + 1).toFloat(),
                unit = "天",
                completed = false,
                metadata = mapOf("currentStreak" to streakDays)
            )
        )

        // 返回未完成或今天可以完成的挑战
        return allChallenges.filterNot { it.completed }.take(3)
    }

    override suspend fun getUserAchievements(userId: Long): List<Achievement> {
        val streakDays = recordRepository.getStreakDays(userId)
        val varietyCount = recordRepository.getFoodVarietyCount(userId, 7)
        val totalRecords = recordRepository.getUserRecordCount(userId)

        // 获取已解锁的成就
        val unlockedAchievements = recommendationRepository.getUserAchievements(userId)
        val unlockedIds = unlockedAchievements.map { it.id }

        val allAchievements = mutableListOf<Achievement>()

        // 一周坚持者
        if (streakDays >= 7) {
            val isUnlocked = unlockedIds.contains(1L)
            allAchievements.add(
                Achievement(
                    id = 1,
                    name = "一周坚持者",
                    description = "连续记录7天饮食",
                    type = AchievementType.MILESTONE,
                    icon = "achievement_streak_7",
                    points = 50,
                    condition = Condition.StreakDays(7),
                    unlockedAt = if (isUnlocked) System.currentTimeMillis() - 86400000 else null
                )
            )
        }

        // 食物探索家
        if (varietyCount >= 20) {
            val isUnlocked = unlockedIds.contains(2L)
            allAchievements.add(
                Achievement(
                    id = 2,
                    name = "食物探索家",
                    description = "一周内摄入20种不同食物",
                    type = AchievementType.MILESTONE,
                    icon = "achievement_food_variety",
                    points = 100,
                    condition = Condition.FoodVariety(20),
                    unlockedAt = if (isUnlocked) System.currentTimeMillis() - 172800000 else null
                )
            )
        }

        // 记录达人
        if (totalRecords >= 50) {
            val isUnlocked = unlockedIds.contains(3L)
            allAchievements.add(
                Achievement(
                    id = 3,
                    name = "记录达人",
                    description = "累计记录50次饮食",
                    type = AchievementType.MILESTONE,
                    icon = "achievement_records_50",
                    points = 150,
                    condition = Condition.TotalRecords(50),
                    unlockedAt = if (isUnlocked) System.currentTimeMillis() - 259200000 else null
                )
            )
        }

        // 健康先锋
        val healthScore = nutritionAnalysisService.getLatestHealthScore(userId)
        if (healthScore >= 85) {
            val isUnlocked = unlockedIds.contains(4L)
            allAchievements.add(
                Achievement(
                    id = 4,
                    name = "健康先锋",
                    description = "健康评分达到85分以上",
                    type = AchievementType.SPECIAL,
                    icon = "achievement_health_score",
                    points = 200,
                    condition = Condition.NutrientTarget("health_score", 85.0),
                    unlockedAt = if (isUnlocked) System.currentTimeMillis() - 345600000 else null
                )
            )
        }

        // 营养均衡成就
        val nutritionalGaps = nutritionAnalysisService.getNutritionalGaps(userId, 30)
        val hasNoMajorGaps = nutritionalGaps.none { it.severity == Severity.SEVERE }
        if (hasNoMajorGaps && totalRecords >= 30) {
            val isUnlocked = unlockedIds.contains(5L)
            allAchievements.add(
                Achievement(
                    id = 5,
                    name = "营养均衡师",
                    description = "连续30天营养摄入均衡",
                    type = AchievementType.MILESTONE,
                    icon = "achievement_nutrient_balance",
                    points = 250,
                    condition = Condition.Composite(
                        listOf(
                            Condition.TotalRecords(30),
                            Condition.NutrientTarget("balance_score", 80.0)
                        )
                    ),
                    unlockedAt = if (isUnlocked) System.currentTimeMillis() - 432000000 else null
                )
            )
        }

        return allAchievements
    }

    override suspend fun markRecommendationRead(recommendationId: Long) {
        recommendationRepository.markAsRead(recommendationId)
    }

    override suspend fun markRecommendationApplied(recommendationId: Long) {
        recommendationRepository.markAsApplied(recommendationId)

        // 获取推荐详情，可能用于更新挑战进度
        val recommendation = recommendationRepository.getRecommendation(recommendationId)
        recommendation?.let {
            // 如果推荐与挑战相关，可以更新挑战进度
            if (it.metadata?.containsKey("challengeId") == true) {
                val challengeId = it.metadata["challengeId"] as? Long
                challengeId?.let { id ->
                    updateChallengeProgress(id, 1.0f)
                }
            }
        }
    }

    override suspend fun updateChallengeProgress(challengeId: Long, progress: Float) {
        // 更新挑战进度
        recommendationRepository.updateChallengeProgress(challengeId, progress)

        // 如果进度达到100%，标记为完成
        if (progress >= 1.0f) {
            recommendationRepository.markChallengeCompleted(challengeId)
        }
    }

    // 新增方法：跟踪改善计划进度
    override suspend fun trackPlanProgress(planId: String, userId: Long) {
        planTracker.trackPlanProgress(planId, userId)
    }

    // 新增方法：标记计划任务完成
    override suspend fun markPlanTaskComplete(planId: String, userId: Long, taskId: String) {
        planTracker.markTaskComplete(planId, userId, taskId)
    }

    // 新增方法：获取当前活跃的计划
    override suspend fun getActivePlans(userId: Long): Flow<List<ImprovementPlan>> {
        return planRepository.getActivePlans(userId)
    }

    // 新增方法：获取计划详情
    override suspend fun getPlanDetails(planId: String, userId: Long): ImprovementPlan? {
        return planRepository.getPlan(userId, planId)
    }

    // 新增方法：生成并保存改善计划
    override suspend fun createImprovementPlan(userId: Long, goalType: GoalType): ImprovementPlan? {
        val context = buildRecommendationContext(userId)
        val goal = context.healthGoals.firstOrNull { it.type == goalType }

        return goal?.let {
            val plan = planGenerator.generatePlanForGoal(it, context, 4)
            planRepository.savePlan(plan)
            plan
        }
    }
}
