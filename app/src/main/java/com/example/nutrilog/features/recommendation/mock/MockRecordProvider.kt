package com.example.nutrilog.features.recommendation.mock

import com.example.nutrilog.data.entities.MealLocation
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.entities.MealType
import com.example.nutrilog.features.recommendation.interfaces.RecordProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.random.Random

class MockRecordProvider : RecordProvider {
    // 模拟一些食物标签
    private val foodTags = listOf(
        "健康饮食", "高蛋白", "素食", "低卡", "高纤维",
        "家常菜", "外卖", "快餐", "轻食", "甜品"
    )

    // 模拟一些地点
    private val locations = listOf(
        MealLocation.HOME,
        MealLocation.RESTAURANT,
        MealLocation.CAFETERIA,
        MealLocation.TAKEAWAY,
        MealLocation.OFFICE
    )

    override suspend fun getUserRecords(userId: Long, days: Int): List<MealRecord> {
        val records = mutableListOf<MealRecord>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (i in 0 until days) {
            val date = LocalDate.now().minusDays(i.toLong())
            val dateStr = date.format(formatter)

            // 模拟不同餐次的记录
            records.addAll(generateDailyRecords(dateStr, userId))
        }

        return records
    }

    override suspend fun getTodayRecords(userId: Long): List<MealRecord> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now().format(formatter)

        return generateDailyRecords(today, userId)
    }

    override suspend fun getStreakDays(userId: Long): Int {
        // 模拟连续打卡天数，返回3-30之间的随机数
        return Random.nextInt(3, 31)
    }

    override suspend fun getFoodVarietyCount(userId: Long, days: Int): Int {
        // 模拟过去days天内的食物种类数量
        return when (days) {
            in 0..7 -> Random.nextInt(8, 20)  // 一周内8-20种
            in 8..30 -> Random.nextInt(20, 40)  // 一个月内20-40种
            else -> Random.nextInt(30, 50)  // 超过一个月
        }
    }

    private fun generateDailyRecords(date: String, userId: Long): List<MealRecord> {
        val records = mutableListOf<MealRecord>()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        // 70%的概率有早餐
        if (Random.nextDouble() < 0.7) {
            records.add(
                MealRecord(
                    userId = userId,
                    date = date,
                    time = String.format("%02d:%02d", Random.nextInt(7, 10), Random.nextInt(0, 60)),
                    mealType = MealType.BREAKFAST,
                    location = if (Random.nextBoolean()) MealLocation.HOME else MealLocation.CAFETERIA,
                    mood = Random.nextInt(3, 6),  // 3-5分
                    note = "早餐：${if (Random.nextBoolean()) "全麦面包+牛奶" else "豆浆+油条"}",
                    calories = Random.nextDouble(300.0, 500.0),
                    tag = if (Random.nextBoolean()) "健康饮食" else "快速早餐"
                )
            )
        }

        // 90%的概率有午餐
        if (Random.nextDouble() < 0.9) {
            records.add(
                MealRecord(
                    userId = userId,
                    date = date,
                    time = String.format("%02d:%02d", Random.nextInt(11, 13), Random.nextInt(0, 60)),
                    mealType = MealType.LUNCH,
                    location = locations.random(),
                    mood = Random.nextInt(3, 6),
                    note = "午餐：${if (Random.nextBoolean()) "米饭+蔬菜+鸡肉" else "面条+汤"}",
                    calories = Random.nextDouble(500.0, 800.0),
                    tag = if (Random.nextBoolean()) "家常菜" else "外卖"
                )
            )
        }

        // 80%的概率有晚餐
        if (Random.nextDouble() < 0.8) {
            records.add(
                MealRecord(
                    userId = userId,
                    date = date,
                    time = String.format("%02d:%02d", Random.nextInt(18, 20), Random.nextInt(0, 60)),
                    mealType = MealType.DINNER,
                    location = if (Random.nextBoolean()) MealLocation.HOME else MealLocation.RESTAURANT,
                    mood = Random.nextInt(2, 6),  // 2-5分
                    note = "晚餐：${if (Random.nextDouble() < 0.7) "米饭+蔬菜+鱼" else "外卖披萨"}",
                    calories = Random.nextDouble(400.0, 700.0),
                    tag = if (Random.nextDouble() < 0.6) "健康饮食" else "社交聚餐"
                )
            )
        }

        // 30%的概率有宵夜
        if (Random.nextDouble() < 0.3) {
            records.add(
                MealRecord(
                    userId = userId,
                    date = date,
                    time = String.format("%02d:%02d", Random.nextInt(21, 24), Random.nextInt(0, 60)),
                    mealType = MealType.SNACK,
                    location = MealLocation.HOME,
                    mood = Random.nextInt(3, 6),
                    note = "宵夜：${if (Random.nextBoolean()) "水果" else "零食"}",
                    calories = Random.nextDouble(100.0, 300.0),
                    tag = "宵夜"
                )
            )
        }

        return records
    }
}