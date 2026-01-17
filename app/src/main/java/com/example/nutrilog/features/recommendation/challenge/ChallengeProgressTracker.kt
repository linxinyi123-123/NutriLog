// app/src/main/java/com/nutrilog/features/recommendation/challenge/ChallengeProgressTracker.kt
package com.example.nutrilog.features.recommendation.challenge

import com.example.nutrilog.data.repository.MealRecordRepository
import com.example.nutrilog.data.repository.FoodRepository
import kotlinx.coroutines.flow.first

/**
 * 挑战进度追踪器
 */
class ChallengeProgressTracker(
    private val challengeRepository: ChallengeRepository,
    private val mealRecordRepository: MealRecordRepository,
    private val foodRepository: FoodRepository
) {

    /**
     * 追踪挑战进度
     */
    suspend fun trackChallengeProgress(userId: Long) {
        val date = java.time.LocalDate.now().toString()
        val challenges = challengeRepository.getDailyChallenges(userId, date).first()

        challenges.forEach { challenge ->
            if (!challenge.completed) {
                val progress = calculateChallengeProgress(challenge)

                if (progress >= challenge.target) {
                    // 挑战完成
                    challengeRepository.updateDailyChallengeProgress(challenge.id, progress)
                    challengeRepository.markDailyChallengeCompleted(challenge.id)

                    // 发送完成通知（简化实现）
                    println("挑战完成: ${challenge.title}, 奖励: ${challenge.rewardPoints}积分")
                } else {
                    // 更新进度
                    challengeRepository.updateDailyChallengeProgress(challenge.id, progress)
                }
            }
        }
    }

    /**
     * 计算挑战进度
     */
    private suspend fun calculateChallengeProgress(challenge: DailyChallenge): Float {
        return when (challenge.type) {
            ChallengeType.MANDATORY -> {
                // 检查是否记录了至少一餐
                val todayRecords = mealRecordRepository.getTodayMealRecords()
                if (todayRecords.isNotEmpty()) {
                    challenge.target // 完成
                } else {
                    0f // 未开始
                }
            }

            ChallengeType.NUTRITION -> {
                val nutrient = challenge.metadata["nutrient"] as? String
                calculateNutrientProgress(nutrient, challenge.target)
            }

            ChallengeType.HABIT -> {
                val habit = challenge.metadata["habit"] as? String
                calculateHabitProgress(habit, challenge.target)
            }

            ChallengeType.MEAL_RECORD -> {
                // 计算记录的餐次数
                val todayRecords = mealRecordRepository.getTodayMealRecords()
                todayRecords.size.toFloat()
            }

            else -> {
                // 其他类型使用随机进度（简化实现）
                0f
            }
        }.coerceIn(0f, challenge.target)
    }

    /**
     * 计算营养进度
     */
    private suspend fun calculateNutrientProgress(nutrient: String?, target: Float): Float {
        // 获取今日的餐记录
        val todayRecords = mealRecordRepository.getTodayMealRecords()
        
        // 计算今日营养成分总和
        var totalNutrient = 0.0
        
        for (record in todayRecords) {
            // 获取该记录的食物列表
            val foodsWithAmount = mealRecordRepository.getFoodsForRecord(record.id)
            
            for ((food, amount) in foodsWithAmount) {
                // 计算单个食物的营养成分：(每100克营养成分 * 克数) / 100
                val nutrientValue = when (nutrient) {
                    "protein" -> food.protein
                    "carbs" -> food.carbs
                    "fat" -> food.fat
                    "fiber" -> food.fiber ?: 0.0
                    else -> 0.0
                }
                totalNutrient += (nutrientValue * amount) / 100
            }
        }
        
        return totalNutrient.toFloat()
    }

    /**
     * 计算习惯进度
     */
    private suspend fun calculateHabitProgress(habit: String?, target: Float): Float {
        val todayRecords = mealRecordRepository.getTodayMealRecords()
        
        return when (habit) {
            "early_breakfast" -> {
                // 检查是否在9点前吃了早餐
                val hasEarlyBreakfast = todayRecords.any { record ->
                    val time = record.time // 假设格式为 HH:mm
                    time < "09:00" && record.mealType.name == "BREAKFAST"
                }
                if (hasEarlyBreakfast) target else 0f
            }
            "early_dinner" -> {
                // 检查是否在20点前吃了晚餐
                val hasEarlyDinner = todayRecords.any { record ->
                    val time = record.time
                    time < "20:00" && record.mealType.name == "DINNER"
                }
                if (hasEarlyDinner) target else 0f
            }
            "drink_water" -> {
                // 简化实现：假设没有专门的喝水记录，返回0
                0f
            }
            else -> {
                0f
            }
        }
    }
}