package com.example.nutrilog.features.recommendation.test

import com.example.nutrilog.features.recommendation.model.GoalType
import com.example.nutrilog.features.recommendation.model.improvement.*
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

class ImprovementPlanTest {

    @Test
    fun `test improvement plan days calculation`() {
        println("=== 测试改善计划天数计算 ===")

        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 1, 31)

        val plan = ImprovementPlan(
            id = "test_plan_1",
            userId = 1,
            title = "30天减重计划",
            goalType = GoalType.WEIGHT_LOSS,
            duration = 30,
            startDate = startDate,
            endDate = endDate,
            totalWeeks = 4,
            weeklyPlans = emptyList()
        )

        println("计划开始日期: ${plan.startDate}")
        println("计划结束日期: ${plan.endDate}")
        println("计划时长: ${plan.duration}天")

        assertTrue("计划时长应为30天", plan.duration == 30)
        println("计划天数计算测试通过！")
        println("=== 测试结束 ===")
    }

    @Test
    fun `test weekly plan progress calculation`() {
        println("=== 测试周计划进度计算 ===")

        // 创建周计划
        val weeklyPlan = WeeklyPlan(
            weekNumber = 1,
            focus = "建立基础习惯",
            description = "本周重点是建立基础的饮食习惯",
            targets = WeeklyTargets(
                calories = 1800.0,
                protein = 70.0,
                vegetables = 5
            ),
            dailyTasks = listOf(
                DailyTask(
                    id = "record_meals",
                    title = "记录所有饮食",
                    description = "准确记录三餐和零食",
                    type = TaskType.RECORDING,
                    isRequired = true
                ),
                DailyTask(
                    id = "control_sugar",
                    title = "控制添加糖",
                    description = "每日添加糖不超过25g",
                    type = TaskType.NUTRITION,
                    isRequired = true
                ),
                DailyTask(
                    id = "drink_water",
                    title = "充足饮水",
                    description = "每日饮水2000ml以上",
                    type = TaskType.NUTRITION,
                    isRequired = true
                ),
                DailyTask(
                    id = "add_vegetables",
                    title = "增加蔬菜",
                    description = "每餐包含一份蔬菜",
                    type = TaskType.NUTRITION,
                    isRequired = false
                ),
                DailyTask(
                    id = "exercise",
                    title = "适度运动",
                    description = "每天运动30分钟",
                    type = TaskType.EXERCISE,
                    isRequired = false
                )
            ),
            successCriteria = listOf(
                "平均每日热量不超标",
                "蛋白质摄入达标",
                "蔬菜摄入达标"
            )
        )

        println("周计划第${weeklyPlan.weekNumber}周")
        println("本周重点: ${weeklyPlan.focus}")
        println("本周描述: ${weeklyPlan.description}")
        println("每日任务数量: ${weeklyPlan.dailyTasks.size}")
        println("必做任务: ${weeklyPlan.dailyTasks.count { it.isRequired }}个")
        println("可选任务: ${weeklyPlan.dailyTasks.count { !it.isRequired }}个")

        // 测试进度计算
        val completedTasks = listOf("record_meals", "control_sugar", "drink_water")
        val progress = weeklyPlan.getProgress(completedTasks)

        println("完成的任务: $completedTasks")
        println("计算出的进度: $progress")

        // 计算预期进度
        val requiredTasks = weeklyPlan.dailyTasks.filter { it.isRequired }
        val completedRequiredTasks = requiredTasks.count { it.id in completedTasks }
        val expectedProgress = if (requiredTasks.isNotEmpty()) {
            completedRequiredTasks.toFloat() / requiredTasks.size
        } else {
            0f
        }

        assertEquals("进度计算正确", expectedProgress, progress)
        println("周计划进度计算测试通过！")
        println("=== 测试结束 ===")
    }

    @Test
    fun `test plan status and goals`() {
        println("=== 测试计划状态和目标类型 ===")

        // 测试所有目标类型
        val goalTypes = GoalType.values()
        println("支持的目标类型:")
        goalTypes.forEach { type ->
            println("- ${type.name}: ${getGoalTypeDescription(type)}")
        }

        // 测试所有计划状态
        val planStatuses = PlanStatus.values()
        println("\n支持的计划状态:")
        planStatuses.forEach { status ->
            println("- ${status.name}")
        }

        // 测试任务类型
        val taskTypes = TaskType.values()
        println("\n支持的任务类型:")
        taskTypes.forEach { type ->
            println("- ${type.name}")
        }

        // 测试任务难度
        val taskDifficulties = TaskDifficulty.values()
        println("\n支持的任务难度:")
        taskDifficulties.forEach { difficulty ->
            println("- ${difficulty.name}")
        }

        assertEquals("目标类型数量", 6, goalTypes.size)
        assertEquals("计划状态数量", 6, planStatuses.size)
        assertEquals("任务类型数量", 6, taskTypes.size)
        assertEquals("任务难度数量", 3, taskDifficulties.size)

        println("\n枚举测试全部通过！")
        println("=== 测试结束 ===")
    }

    private fun getGoalTypeDescription(type: GoalType): String {
        return when (type) {
            GoalType.WEIGHT_LOSS -> "减重"
            GoalType.WEIGHT_GAIN -> "增重"
            GoalType.MUSCLE_GAIN -> "增肌"
            GoalType.BODY_FAT_REDUCTION -> "减脂"
            GoalType.HEALTH_IMPROVEMENT -> "健康改善"
            GoalType.NUTRIENT_BALANCE -> "营养均衡"
        }
    }

    @Test
    fun `test improvement plan creation`() {
        println("=== 测试改善计划创建 ===")

        val plan = createSampleImprovementPlan()

        println("计划ID: ${plan.id}")
        println("计划标题: ${plan.title}")
        println("目标类型: ${plan.goalType}")
        println("关联健康目标ID: ${plan.healthGoalId ?: "无"}")
        println("总时长: ${plan.duration}天")
        println("总周数: ${plan.totalWeeks}")
        println("当前周数: ${plan.currentWeek}")
        println("计划状态: ${plan.status}")
        println("进度: ${plan.progress * 100}%")
        println("已完成的周数: ${plan.completedWeeks}")
        println("里程碑数量: ${plan.milestones.size}")

        assertNotNull("计划ID不应为空", plan.id)
        assertTrue("计划标题不应为空", plan.title.isNotEmpty())
        assertTrue("总周数应大于0", plan.totalWeeks > 0)
        assertTrue("进度应在0-1之间", plan.progress in 0f..1f)

        println("改善计划创建测试通过！")
        println("=== 测试结束 ===")
    }

    private fun createSampleImprovementPlan(): ImprovementPlan {
        return ImprovementPlan(
            id = "plan_${System.currentTimeMillis()}",
            userId = 1,
            title = "28天营养均衡计划",
            goalType = GoalType.NUTRIENT_BALANCE,
            healthGoalId = 101,
            duration = 28,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(28),
            currentWeek = 2,
            totalWeeks = 4,
            weeklyPlans = listOf(
                WeeklyPlan(
                    weekNumber = 1,
                    focus = "建立基础习惯",
                    description = "第一周重点是建立基础记录习惯",
                    targets = WeeklyTargets(calories = 2000.0, protein = 65.0),
                    dailyTasks = listOf(
                        DailyTask(
                            id = "task1",
                            title = "记录饮食",
                            description = "每天记录三餐",
                            type = TaskType.RECORDING,
                            isRequired = true
                        ),
                        DailyTask(
                            id = "task2",
                            title = "控制糖分",
                            description = "减少添加糖摄入",
                            type = TaskType.NUTRITION,
                            isRequired = true
                        )
                    ),
                    successCriteria = listOf("完成基础记录")
                ),
                WeeklyPlan(
                    weekNumber = 2,
                    focus = "增加蛋白质",
                    description = "第二周重点是增加蛋白质摄入",
                    targets = WeeklyTargets(protein = 75.0),
                    dailyTasks = listOf(
                        DailyTask(
                            id = "task3",
                            title = "高蛋白早餐",
                            description = "早餐包含蛋白质",
                            type = TaskType.NUTRITION,
                            isRequired = true
                        ),
                        DailyTask(
                            id = "task4",
                            title = "优质蛋白来源",
                            description = "选择瘦肉、豆制品等",
                            type = TaskType.NUTRITION,
                            isRequired = true
                        )
                    ),
                    successCriteria = listOf("蛋白质摄入达标")
                )
            ),
            status = PlanStatus.ACTIVE,
            progress = 0.35f,
            completedWeeks = setOf(1),
            milestones = listOf(
                Milestone(
                    id = "milestone_1",
                    title = "第一周完成",
                    description = "成功建立基础记录习惯",
                    weekNumber = 1,
                    rewardPoints = 50,
                    achieved = true,
                    achievedAt = LocalDate.now().minusDays(3)
                )
            ),
            notes = "这是一个测试计划"
        )
    }

    @Test
    fun `test daily task creation`() {
        println("=== 测试每日任务创建 ===")

        // 测试创建 DailyTask
        val task1 = DailyTask(
            id = "test_task_1",
            title = "测试任务",
            description = "这是一个测试任务",
            type = TaskType.OTHER,
            isRequired = true
        )

        val task2 = DailyTask(
            id = "test_task_2",
            title = "可选任务",
            description = "这是一个可选任务",
            type = TaskType.OTHER,
            isRequired = false
        )

        println("任务1: ${task1.title}, 必做: ${task1.isRequired}, 类型: ${task1.type}")
        println("任务2: ${task2.title}, 必做: ${task2.isRequired}, 类型: ${task2.type}")

        assertEquals("任务1应该是必做", true, task1.isRequired)
        assertEquals("任务2应该不是必做", false, task2.isRequired)
        assertTrue("任务描述不应为空", task1.description.isNotEmpty())

        // 测试任务进度计算
        val progress1 = task1.getProgress()
        println("任务1进度（无目标值）: $progress1")
        assertEquals("未完成任务进度应为0", 0f, progress1)

        println("每日任务创建测试通过！")
        println("=== 测试结束 ===")
    }

    @Test
    fun `test weekly targets description`() {
        println("=== 测试每周目标描述 ===")

        val targets1 = WeeklyTargets(
            calories = 1800.0,
            protein = 70.0,
            fiber = 25.0,
            vegetables = 5,
            water = 2.0
        )

        val targets2 = WeeklyTargets(
            protein = 80.0,
            vegetables = 7
        )

        val targets3 = WeeklyTargets() // 空目标

        println("目标1描述: ${targets1.getDescription()}")
        println("目标2描述: ${targets2.getDescription()}")
        println("目标3描述: ${targets3.getDescription()}")

        assertTrue("目标1描述应包含热量", targets1.getDescription().contains("热量"))
        assertTrue("目标1描述应包含蛋白质", targets1.getDescription().contains("蛋白质"))
        assertTrue("目标2描述应包含蛋白质", targets2.getDescription().contains("蛋白质"))
        assertTrue("目标2描述应包含蔬菜", targets2.getDescription().contains("蔬菜"))
        assertTrue("空目标描述应为空", targets3.getDescription().isEmpty())

        println("每周目标描述测试通过！")
        println("=== 测试结束 ===")
    }

    @Test
    fun `test task progress calculation`() {
        println("=== 测试任务进度计算 ===")

        val task1 = DailyTask(
            id = "task1",
            title = "饮水任务",
            description = "每日饮水2000ml",
            type = TaskType.NUTRITION,
            isRequired = true,
            targetValue = 2000.0,
            unit = "ml"
        )

        val task2 = DailyTask(
            id = "task2",
            title = "运动任务",
            description = "每日运动30分钟",
            type = TaskType.EXERCISE,
            isRequired = true,
            targetValue = 30.0,
            unit = "分钟"
        )

        // 测试未完成任务
        println("任务1未完成进度: ${task1.getProgress()}")
        println("任务2未完成进度: ${task2.getProgress()}")
        assertEquals("未完成任务的进度应为0", 0f, task1.getProgress())
        assertEquals("未完成任务的进度应为0", 0f, task2.getProgress())

        // 测试有当前值的进度
        println("任务1饮水1500ml进度: ${task1.getProgress(1500.0)}")
        println("任务2运动15分钟进度: ${task2.getProgress(15.0)}")
        assertEquals("1500/2000的进度应为0.75", 0.75f, task1.getProgress(1500.0))
        assertEquals("15/30的进度应为0.5", 0.5f, task2.getProgress(15.0))

        // 测试进度不超过1
        println("任务1饮水2500ml进度: ${task1.getProgress(2500.0)}")
        assertEquals("超额完成进度最大为1", 1f, task1.getProgress(2500.0))

        println("任务进度计算测试通过！")
        println("=== 测试结束 ===")
    }

    @Test
    fun `test task with target value`() {
        println("=== 测试带目标值的任务 ===")

        // 创建带目标值的任务
        val task2 = DailyTask(
            id = "test_task_2",
            title = "可选任务",
            description = "这是一个可选任务",
            type = TaskType.OTHER,
            isRequired = false,
            targetValue = 10.0, // 创建时指定目标值
            unit = "次"
        )

        println("任务: ${task2.title}, 目标值: ${task2.targetValue}, 单位: ${task2.unit}")

        // 测试进度计算
        println("任务2进度（5/10）: ${task2.getProgress(5.0)}")
        assertEquals("5/10的任务进度应为0.5", 0.5f, task2.getProgress(5.0))

        println("带目标值的任务测试通过！")
        println("=== 测试结束 ===")
    }
}