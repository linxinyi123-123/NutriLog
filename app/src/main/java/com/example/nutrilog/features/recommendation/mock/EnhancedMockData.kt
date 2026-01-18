package com.example.nutrilog.features.recommendation.mock

import com.example.nutrilog.features.recommendation.model.*
import com.example.nutrilog.features.recommendation.model.gamification.*
import com.example.nutrilog.features.recommendation.challenge.*
import com.example.nutrilog.features.recommendation.model.improvement.*
import java.time.LocalDate
import java.util.*
// 在 EnhancedMockData.kt 文件顶部添加以下导入
import com.example.nutrilog.features.recommendation.model.improvement.Milestone
import com.example.nutrilog.features.recommendation.model.improvement.PlanStatus
import com.example.nutrilog.features.recommendation.model.improvement.TaskType

object EnhancedMockData {

    // 生成多样化的推荐
    fun generateDiverseRecommendations(userId: Long): List<Recommendation> {
        return listOf(
            // 1. 营养缺口类推荐
            Recommendation(
                id = 1,
                type = RecommendationType.NUTRITION_GAP,
                title = "蛋白质摄入不足",
                description = "最近7天蛋白质平均摄入45g，低于推荐值70g。建议增加鸡蛋、鸡胸肉、豆腐等食物。",
                priority = Priority.HIGH,
                confidence = 0.9f,
                reason = "基于最近7天营养分析",
                actions = listOf(
                    Action.ShowFoodDetails(101),
                    Action.AddToMealPlan(listOf(101, 102, 103))
                ),
                metadata = mapOf(
                    "nutrient" to "protein",
                    "gapPercentage" to 35.7
                )
            ),

            // 2. 目标类推荐
            Recommendation(
                id = 2,
                type = RecommendationType.MEAL_PLAN,
                title = "减重期间热量控制",
                description = "今日热量摄入1800kcal，接近目标值1900kcal。建议晚餐选择低热量食物。",
                priority = Priority.MEDIUM,
                confidence = 0.7f,
                reason = "基于减重目标的热量监控",
                actions = listOf(
                    Action.ShowFoodDetails(201),
                    Action.DismissRecommendation("已了解")
                ),
                metadata = mapOf(
                    "goalType" to "WEIGHT_LOSS",
                    "currentCalories" to 1800,
                    "targetCalories" to 1900
                )
            ),

            // 3. 时间类推荐
            Recommendation(
                id = 3,
                type = RecommendationType.FOOD_SUGGESTION,
                title = "早餐时间到",
                description = "现在是早餐时间，推荐营养早餐：燕麦粥+牛奶+水果，准备时间约15分钟。",
                priority = Priority.MEDIUM,
                confidence = 0.8f,
                reason = "基于当前时间和您的习惯",
                actions = listOf(
                    Action.AddToMealPlan(listOf(301, 302, 303)),
                    Action.DismissRecommendation("稍后提醒")
                ),
                metadata = mapOf(
                    "mealType" to "breakfast",
                    "prepTime" to "15分钟"
                )
            ),

            // 4. 地点类推荐
            Recommendation(
                id = 4,
                type = RecommendationType.FOOD_SUGGESTION,
                title = "食堂高蛋白选择",
                description = "检测到您在食堂用餐，推荐红烧鸡块+青菜，蛋白质含量高且热量适中。",
                priority = Priority.LOW,
                confidence = 0.6f,
                reason = "基于您的当前位置和食堂菜品",
                actions = listOf(
                    Action.AddToMealPlan(listOf(401)),
                    Action.DismissRecommendation("不喜欢")
                ),
                metadata = mapOf(
                    "location" to "cafeteria",
                    "proteinContent" to 25.5
                )
            ),

            // 5. 教育类推荐
            Recommendation(
                id = 5,
                type = RecommendationType.EDUCATIONAL,
                title = "了解膳食纤维的重要性",
                description = "膳食纤维有助于消化和控制体重。建议每天摄入25-30g，可从蔬菜、水果、全谷物中获取。",
                priority = Priority.LOW,
                confidence = 1.0f,
                reason = "基于您的营养知识需求",
                actions = listOf(
                    Action.DismissRecommendation("已了解")
                ),
                metadata = mapOf(
                    "educationalType" to "nutrient_knowledge",
                    "nutrient" to "fiber"
                )
            )
        )
    }

    // 生成完整的成就列表
    fun generateAllAchievements(): List<Achievement> {
        return listOf(
            // 记录类成就
            Achievement(
                id = 1,
                name = "首次记录",
                description = "记录你的第一餐饮食",
                type = AchievementType.DAILY,
                icon = "achievement_first_log",
                points = 10,
                condition = Condition.StreakDays(1),
                unlockedAt = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
            ),
            Achievement(
                id = 2,
                name = "一周坚持者",
                description = "连续7天记录饮食",
                type = AchievementType.MILESTONE,
                icon = "achievement_streak_7",
                points = 50,
                condition = Condition.StreakDays(7),
                unlockedAt = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000
            ),
            Achievement(
                id = 3,
                name = "月度记录达人",
                description = "连续30天记录饮食",
                type = AchievementType.MILESTONE,
                icon = "achievement_streak_30",
                points = 200,
                condition = Condition.StreakDays(30),
                unlockedAt = null
            ),

            // 营养类成就
            Achievement(
                id = 4,
                name = "蛋白质达人",
                description = "一周内每日蛋白质摄入达标",
                type = AchievementType.SPECIAL,
                icon = "achievement_protein_master",
                points = 100,
                condition = Condition.NutrientTarget("protein", 60.0),
                unlockedAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000
            ),
            Achievement(
                id = 5,
                name = "蔬果爱好者",
                description = "一周内每日摄入5种以上蔬果",
                type = AchievementType.SPECIAL,
                icon = "achievement_vegetable_lover",
                points = 80,
                condition = Condition.FoodVariety(5),
                unlockedAt = null
            ),

            // 探索类成就
            Achievement(
                id = 6,
                name = "食物探索家",
                description = "尝试过50种不同食物",
                type = AchievementType.SPECIAL,
                icon = "achievement_food_explorer",
                points = 150,
                condition = Condition.TotalRecords(50),
                unlockedAt = null
            ),
            Achievement(
                id = 7,
                name = "健康食谱家",
                description = "创建并完成10个健康食谱",
                type = AchievementType.SECRET,
                icon = "achievement_recipe_master",
                points = 180,
                condition = Condition.Composite(
                    listOf(
                        Condition.TotalRecords(10),
                        Condition.NutrientTarget("health_score", 80.0)
                    )
                ),
                unlockedAt = null
            )
        )
    }

    // 生成改善计划
    fun generateImprovementPlan(userId: Long): ImprovementPlan {
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(28)

        return ImprovementPlan(
            id = "plan_${userId}_${System.currentTimeMillis()}",
            userId = userId,
            title = "四周减重计划",
            goalType = GoalType.WEIGHT_LOSS,
            duration = 28,
            startDate = startDate,
            endDate = endDate,
            totalWeeks = 4,
            weeklyPlans = (1..4).map { weekNumber ->
                WeeklyPlan(
                    weekNumber = weekNumber,
                    focus = when(weekNumber) {
                        1 -> "建立基础习惯"
                        2 -> "控制热量摄入"
                        3 -> "优化饮食结构"
                        4 -> "巩固健康习惯"
                        else -> "持续改进"
                    },
                    description = "第${weekNumber}周改善计划",
                    targets = WeeklyTargets(
                        calories = 1800.0,
                        protein = 70.0,
                        vegetables = 5
                    ),
                    dailyTasks = listOf(
                        DailyTask(
                            id = "task_${weekNumber}_1",
                            title = "记录所有饮食",
                            description = "准确记录三餐和零食",
                            type = TaskType.RECORDING,
                            isRequired = true
                        ),
                        DailyTask(
                            id = "task_${weekNumber}_2",
                            title = "控制添加糖",
                            description = "每日添加糖不超过25g",
                            type = TaskType.NUTRITION,
                            isRequired = true
                        ),
                        DailyTask(
                            id = "task_${weekNumber}_3",
                            title = "充足饮水",
                            description = "每日饮水2000ml以上",
                            type = TaskType.HABIT,
                            isRequired = false
                        )
                    ),
                    successCriteria = listOf(
                        "平均每日热量不超标",
                        "蛋白质摄入达标",
                        "蔬菜摄入达标"
                    )
                )
            },
            status = PlanStatus.ACTIVE,
            milestones = listOf(
                Milestone(
                    id = "milestone_1",
                    title = "第一周习惯建立",
                    description = "成功建立每日记录习惯",
                    weekNumber = 1,
                    rewardPoints = 50,
                    achieved = true,
                    achievedAt = startDate.plusDays(7)
                ),
                Milestone(
                    id = "milestone_2",
                    title = "热量控制达标",
                    description = "连续两周热量控制达标",
                    weekNumber = 2,
                    rewardPoints = 100,
                    achieved = true,
                    achievedAt = startDate.plusDays(14)
                ),
                Milestone(
                    id = "milestone_3",
                    title = "营养均衡达成",
                    description = "实现营养素均衡摄入",
                    weekNumber = 3,
                    rewardPoints = 150,
                    achieved = false
                ),
                Milestone(
                    id = "milestone_4",
                    title = "计划完成",
                    description = "完成四周减重计划",
                    weekNumber = 4,
                    rewardPoints = 200,
                    achieved = false
                )
            )
        )
    }

    // 生成用户统计数据
    fun generateUserStats() = UserStats(
        totalPoints = 340,
        level = 3,
        unlockedAchievements = 4,
        totalAchievements = 7,
        completedChallenges = 12,
        longestStreak = 7,
        currentStreak = 5
    )

    data class UserStats(
        val totalPoints: Int,
        val level: Int,
        val unlockedAchievements: Int,
        val totalAchievements: Int,
        val completedChallenges: Int,
        val longestStreak: Int,
        val currentStreak: Int
    )
}