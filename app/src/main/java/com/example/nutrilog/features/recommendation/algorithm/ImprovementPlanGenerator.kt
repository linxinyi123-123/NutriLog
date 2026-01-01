package com.example.nutrilog.features.recommendation.algorithm

import com.example.nutrilog.features.recommendation.engine.RecommendationContext
import com.example.nutrilog.features.recommendation.model.GoalType
import com.example.nutrilog.features.recommendation.model.HealthGoal
import com.example.nutrilog.features.recommendation.model.improvement.*
import java.time.LocalDate
import java.util.UUID
import com.example.nutrilog.features.recommendation.interfaces.Severity

/**
 * 改善计划生成器
 */
class ImprovementPlanGenerator {

    /**
     * 为健康目标生成改善计划
     */
    fun generatePlanForGoal(
        goal: HealthGoal,
        context: RecommendationContext,
        durationWeeks: Int = 4
    ): ImprovementPlan {
        // 分析当前状态
        val currentState = analyzeCurrentState(context, goal)

        // 生成周计划
        val weeklyPlans = generateWeeklyPlans(goal.type, durationWeeks, currentState)

        // 生成每日任务模板
        val dailyTemplates = generateDailyTemplates(goal.type)

        // 生成里程碑
        val milestones = generateMilestones(goal, durationWeeks)

        // 计算开始和结束日期
        val startDate = LocalDate.now()
        val endDate = startDate.plusWeeks(durationWeeks.toLong())

        return ImprovementPlan(
            id = generatePlanId(goal.userId),
            userId = goal.userId,
            title = generatePlanTitle(goal.type, durationWeeks),
            goalType = goal.type,
            healthGoalId = goal.id,
            duration = durationWeeks * 7,
            startDate = startDate,
            endDate = endDate,
            totalWeeks = durationWeeks,
            weeklyPlans = weeklyPlans,
            dailyTemplates = dailyTemplates,
            milestones = milestones
        )
    }

    /**
     * 分析当前状态
     */
    private fun analyzeCurrentState(
        context: RecommendationContext,
        goal: HealthGoal
    ): CurrentState {
        // 分析营养缺口
        val nutritionalGaps = context.nutritionalGaps ?: emptyList()
        val severeGaps = nutritionalGaps.filter { it.severity == Severity.SEVERE }

        // 分析饮食习惯
        val mealPatterns = context.mealPatterns
        val foodVariety = mealPatterns?.foodVariety ?: 0

        // 分析记录习惯
        val recordConsistency = calculateRecordConsistency(context)

        return CurrentState(
            nutritionalGaps = severeGaps.map { it.nutrient },
            foodVariety = foodVariety,
            recordConsistency = recordConsistency,
            healthScore = context.healthScore,
            currentWeight = null // 需要从健康目标获取
        )
    }

    /**
     * 生成周计划
     */
    private fun generateWeeklyPlans(
        goalType: GoalType,
        totalWeeks: Int,
        currentState: CurrentState
    ): List<WeeklyPlan> {
        return (1..totalWeeks).map { weekNumber ->
            generateWeeklyPlan(weekNumber, goalType, totalWeeks, currentState)
        }
    }

    /**
     * 生成单个周计划
     */
    private fun generateWeeklyPlan(
        weekNumber: Int,
        goalType: GoalType,
        totalWeeks: Int,
        currentState: CurrentState
    ): WeeklyPlan {
        return when (goalType) {
            GoalType.WEIGHT_LOSS -> generateWeightLossWeeklyPlan(weekNumber, totalWeeks, currentState)
            GoalType.MUSCLE_GAIN -> generateMuscleGainWeeklyPlan(weekNumber, totalWeeks, currentState)
            GoalType.NUTRIENT_BALANCE -> generateNutrientBalanceWeeklyPlan(weekNumber, totalWeeks, currentState)
            GoalType.HEALTH_IMPROVEMENT -> generateHealthImprovementWeeklyPlan(weekNumber, totalWeeks, currentState)
            else -> generateGeneralWeeklyPlan(weekNumber, totalWeeks, currentState)
        }
    }

    /**
     * 生成减重周计划
     */
    private fun generateWeightLossWeeklyPlan(
        weekNumber: Int,
        totalWeeks: Int,
        currentState: CurrentState
    ): WeeklyPlan {
        val weeklyFocus = when (weekNumber) {
            1 -> "建立基础习惯"
            2 -> "控制热量摄入"
            3 -> "优化饮食结构"
            4 -> "巩固健康习惯"
            else -> "持续改进"
        }

        val description = when (weekNumber) {
            1 -> "本周重点：建立规律的饮食记录习惯，了解自己的饮食模式"
            2 -> "本周重点：学习控制总热量摄入，避免高热量食物"
            3 -> "本周重点：优化饮食结构，增加蔬菜和蛋白质摄入"
            4 -> "本周重点：巩固健康习惯，保持减重成果"
            else -> "继续坚持健康饮食"
        }

        // 周目标
        val targets = WeeklyTargets(
            calories = 1800.0 - (weekNumber - 1) * 100,
            protein = 70.0,
            fiber = 25.0,
            vegetables = 5,
            water = 2.0
        )

        // 每日任务
        val dailyTasks = listOf(
            DailyTask(
                id = "record_all_meals_$weekNumber",
                title = "记录所有饮食",
                description = "准确记录三餐和零食",
                type = TaskType.RECORDING,
                isRequired = true
            ),
            DailyTask(
                id = "control_added_sugar_$weekNumber",
                title = "控制添加糖",
                description = "每日添加糖不超过25g",
                type = TaskType.NUTRITION,
                isRequired = true,
                targetValue = 25.0,
                unit = "g"
            ),
            DailyTask(
                id = "increase_vegetables_$weekNumber",
                title = "增加蔬菜摄入",
                description = "每餐至少包含一份蔬菜",
                type = TaskType.NUTRITION,
                isRequired = true,
                targetValue = 5.0,
                unit = "份"
            ),
            DailyTask(
                id = "water_intake_$weekNumber",
                title = "饮水达标",
                description = "每日饮水2000ml以上",
                type = TaskType.HABIT,
                isRequired = true,
                targetValue = 2000.0,
                unit = "ml"
            ),
            DailyTask(
                id = "evening_light_meal_$weekNumber",
                title = "晚餐不过饱",
                description = "晚餐热量不超过500kcal",
                type = TaskType.NUTRITION,
                isRequired = false,
                targetValue = 500.0,
                unit = "kcal"
            )
        )

        // 成功标准
        val successCriteria = listOf(
            "平均每日热量不超标",
            "蔬菜摄入达标5天以上",
            "无暴饮暴食记录",
            "完成至少4天完整记录"
        )

        // 小贴士
        val tips = listOf(
            "饭前喝一杯水有助于控制食欲",
            "细嚼慢咽，每餐至少吃20分钟",
            "避免边看电视边吃饭"
        )

        return WeeklyPlan(
            weekNumber = weekNumber,
            focus = weeklyFocus,
            description = description,
            targets = targets,
            dailyTasks = dailyTasks,
            successCriteria = successCriteria,
            tips = if (weekNumber == 1) tips else emptyList()
        )
    }

    /**
     * 生成增肌周计划
     */
    private fun generateMuscleGainWeeklyPlan(
        weekNumber: Int,
        totalWeeks: Int,
        currentState: CurrentState
    ): WeeklyPlan {
        val weeklyFocus = when (weekNumber) {
            1 -> "增加蛋白质摄入"
            2 -> "合理安排餐次"
            3 -> "营养均衡搭配"
            4 -> "巩固增肌习惯"
            else -> "持续增肌"
        }

        val targets = WeeklyTargets(
            calories = 2500.0 + (weekNumber - 1) * 100,
            protein = 100.0 + (weekNumber - 1) * 10,
            carbs = 300.0,
            vegetables = 4
        )

        val dailyTasks = listOf(
            DailyTask(
                id = "protein_intake_$weekNumber",
                title = "蛋白质达标",
                description = "每日蛋白质摄入达标",
                type = TaskType.NUTRITION,
                isRequired = true,
                targetValue = 100.0 + (weekNumber - 1) * 10,
                unit = "g"
            ),
            DailyTask(
                id = "regular_meals_$weekNumber",
                title = "规律三餐",
                description = "按时吃三餐，避免长时间空腹",
                type = TaskType.HABIT,
                isRequired = true
            ),
            DailyTask(
                id = "post_workout_nutrition_$weekNumber",
                title = "训练后营养",
                description = "训练后30分钟内补充蛋白质和碳水",
                type = TaskType.NUTRITION,
                isRequired = false
            )
        )

        return WeeklyPlan(
            weekNumber = weekNumber,
            focus = weeklyFocus,
            description = "增肌需要充足的热量和蛋白质，同时保证训练质量",
            targets = targets,
            dailyTasks = dailyTasks,
            successCriteria = listOf("蛋白质摄入达标", "热量摄入充足", "训练规律")
        )
    }

    /**
     * 生成营养均衡周计划
     */
    private fun generateNutrientBalanceWeeklyPlan(
        weekNumber: Int,
        totalWeeks: Int,
        currentState: CurrentState
    ): WeeklyPlan {
        // 根据当前营养缺口生成针对性计划
        val focusNutrients = currentState.nutritionalGaps.take(3)

        val weeklyFocus = when (weekNumber) {
            1 -> "多样化饮食"
            2 -> "针对性补充"
            3 -> "优化食物组合"
            4 -> "建立均衡习惯"
            else -> "保持均衡"
        }

        val dailyTasks = mutableListOf<DailyTask>()

        // 针对每个营养缺口生成任务
        focusNutrients.forEachIndexed { index, nutrient ->
            dailyTasks.add(
                DailyTask(
                    id = "improve_${nutrient}_$weekNumber",
                    title = "改善${nutrient}摄入",
                    description = "增加富含${nutrient}的食物",
                    type = TaskType.NUTRITION,
                    isRequired = true
                )
            )
        }

        // 通用任务
        dailyTasks.addAll(listOf(
            DailyTask(
                id = "food_variety_$weekNumber",
                title = "食物多样化",
                description = "每天尝试至少10种不同食物",
                type = TaskType.NUTRITION,
                isRequired = true,
                targetValue = 10.0,
                unit = "种"
            ),
            DailyTask(
                id = "colorful_plate_$weekNumber",
                title = "多彩餐盘",
                description = "每餐包含至少3种颜色的食物",
                type = TaskType.NUTRITION,
                isRequired = false
            )
        ))

        return WeeklyPlan(
            weekNumber = weekNumber,
            focus = weeklyFocus,
            description = "通过多样化饮食实现营养均衡，特别关注：${focusNutrients.joinToString("、")}",
            targets = WeeklyTargets(
                foodVariety = 10,
                vegetables = 5,
                fruits = 3
            ),
            dailyTasks = dailyTasks,
            successCriteria = listOf("食物种类达标", "营养缺口改善")
        )
    }

    /**
     * 生成健康改善周计划
     */
    private fun generateHealthImprovementWeeklyPlan(
        weekNumber: Int,
        totalWeeks: Int,
        currentState: CurrentState
    ): WeeklyPlan {
        val weeklyFocus = when (weekNumber) {
            1 -> "建立基础"
            2 -> "改善习惯"
            3 -> "优化细节"
            4 -> "全面提升"
            else -> "保持健康"
        }

        val dailyTasks = listOf(
            DailyTask(
                id = "regular_schedule_$weekNumber",
                title = "规律作息",
                description = "保持规律的睡眠和饮食时间",
                type = TaskType.HABIT,
                isRequired = true
            ),
            DailyTask(
                id = "stress_management_$weekNumber",
                title = "压力管理",
                description = "每天进行10分钟放松活动",
                type = TaskType.HABIT,
                isRequired = false,
                targetValue = 10.0,
                unit = "分钟"
            ),
            DailyTask(
                id = "mindful_eating_$weekNumber",
                title = "正念饮食",
                description = "至少一餐专心吃饭，不看手机",
                type = TaskType.HABIT,
                isRequired = true
            )
        )

        return WeeklyPlan(
            weekNumber = weekNumber,
            focus = weeklyFocus,
            description = "全面改善生活习惯，提升整体健康水平",
            targets = WeeklyTargets(
                water = 2.0,
                mealsRecorded = 7
            ),
            dailyTasks = dailyTasks,
            successCriteria = listOf("规律作息", "压力管理", "正念饮食")
        )
    }

    /**
     * 生成通用周计划
     */
    private fun generateGeneralWeeklyPlan(
        weekNumber: Int,
        totalWeeks: Int,
        currentState: CurrentState
    ): WeeklyPlan {
        return WeeklyPlan(
            weekNumber = weekNumber,
            focus = "健康改善",
            description = "通过健康饮食和生活习惯改善整体健康",
            targets = WeeklyTargets(
                vegetables = 4,
                water = 1.5,
                mealsRecorded = 5
            ),
            dailyTasks = listOf(
                DailyTask(
                    id = "basic_recording_$weekNumber",
                    title = "基础记录",
                    description = "记录主要餐次",
                    type = TaskType.RECORDING,
                    isRequired = true
                )
            ),
            successCriteria = listOf("完成基础记录")
        )
    }

    /**
     * 生成每日任务模板
     */
    private fun generateDailyTemplates(goalType: GoalType): List<DailyTask> {
        return when (goalType) {
            GoalType.WEIGHT_LOSS -> listOf(
                DailyTask(
                    id = "morning_water",
                    title = "早晨饮水",
                    description = "起床后喝一杯温水",
                    type = TaskType.HABIT,
                    difficulty = TaskDifficulty.EASY
                ),
                DailyTask(
                    id = "evening_walk",
                    title = "晚间散步",
                    description = "晚餐后散步15分钟",
                    type = TaskType.EXERCISE,
                    difficulty = TaskDifficulty.EASY
                )
            )
            GoalType.MUSCLE_GAIN -> listOf(
                DailyTask(
                    id = "post_workout_meal",
                    title = "训练后加餐",
                    description = "训练后及时补充营养",
                    type = TaskType.NUTRITION,
                    difficulty = TaskDifficulty.MEDIUM
                )
            )
            else -> emptyList()
        }
    }

    /**
     * 生成里程碑
     */
    private fun generateMilestones(goal: HealthGoal, totalWeeks: Int): List<Milestone> {
        return listOf(
            Milestone(
                id = "first_week_complete",
                title = "第一周完成",
                description = "成功完成第一周计划",
                weekNumber = 1,
                rewardPoints = 50
            ),
            Milestone(
                id = "halfway_point",
                title = "计划过半",
                description = "完成一半的计划周数",
                weekNumber = totalWeeks / 2,
                rewardPoints = 100
            ),
            Milestone(
                id = "plan_complete",
                title = "计划完成",
                description = "成功完成整个改善计划",
                weekNumber = totalWeeks,
                rewardPoints = 200
            )
        )
    }

    /**
     * 计算记录一致性
     */
    private fun calculateRecordConsistency(context: RecommendationContext): Float {
        // 简化实现：返回一个假设值
        // 实际应该分析最近的记录数据
        return 0.6f
    }

    /**
     * 生成计划ID
     */
    private fun generatePlanId(userId: Long): String {
        return "plan_${userId}_${UUID.randomUUID().toString().substring(0, 8)}"
    }

    /**
     * 生成计划标题
     */
    private fun generatePlanTitle(goalType: GoalType, durationWeeks: Int): String {
        return when (goalType) {
            GoalType.WEIGHT_LOSS -> "${durationWeeks}周减重计划"
            GoalType.MUSCLE_GAIN -> "${durationWeeks}周增肌计划"
            GoalType.NUTRIENT_BALANCE -> "${durationWeeks}周营养均衡计划"
            GoalType.HEALTH_IMPROVEMENT -> "${durationWeeks}周健康改善计划"
            else -> "${durationWeeks}周改善计划"
        }
    }
}

/**
 * 当前状态分析
 */
private data class CurrentState(
    val nutritionalGaps: List<String>, // 严重缺乏的营养素
    val foodVariety: Int,              // 食物种类数
    val recordConsistency: Float,      // 记录一致性（0-1）
    val healthScore: Int,              // 健康评分
    val currentWeight: Double?         // 当前体重
)

// 为简化代码，添加Severity枚举的引用
