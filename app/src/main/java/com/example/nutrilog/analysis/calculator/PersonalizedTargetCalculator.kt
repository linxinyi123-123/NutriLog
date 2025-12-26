package com.example.nutrilog.analysis.calculator

import com.example.nutrilog.common.models.ActivityLevel
import com.example.nutrilog.common.models.Gender
import com.example.nutrilog.common.models.UserProfile
import com.example.nutrilog.shared.NutritionTarget
import com.example.nutrilog.shared.Range

class PersonalizedTargetCalculator {
    fun calculateTargets(user: UserProfile): NutritionTarget {
        // 计算基础代谢率(BMR)
        val bmr = calculateBMR(user)

        // 根据活动水平计算每日总消耗(TDEE)
        val tdee = bmr * getActivityMultiplier(user.activityLevel)

        // 根据目标（维持/减重/增肌）调整
        return adjustForGoal(tdee, user)
    }

    private fun calculateBMR(user: UserProfile): Double {
        // Mifflin-St Jeor公式
        return if (user.gender ==  Gender.MALE) {
            10 * user.weight + 6.25 * user.height - 5 * user.age + 5
        } else {
            10 * user.weight + 6.25 * user.height - 5 * user.age - 161
        }
    }

    //Mifflin-St Jeor 公式的常用标准
    private fun getActivityMultiplier(level: ActivityLevel): Double {
        return when (level) {
            ActivityLevel.SEDENTARY -> 1.2 //久坐
            ActivityLevel.LIGHT -> 1.375 //轻度活跃
            ActivityLevel.MODERATE -> 1.55 //中度活跃
            ActivityLevel.ACTIVE -> 1.725 //活跃
            ActivityLevel.VERY_ACTIVE -> 1.9 //非常活跃
        }
    }

    private fun adjustForGoal(tdee: Double, user: UserProfile): NutritionTarget {
        // 基于年龄、性别、活动水平的综合判断
        val baseCalories = tdee

        // 年龄调整
        val ageAdjustedCalories = when {
            user.age < 25 -> baseCalories + 100   // 年轻人代谢高
            user.age > 50 -> baseCalories - 100   // 年长者代谢低
            else -> baseCalories
        }

        // 性别和活动水平调整蛋白质
        val proteinMultiplier = when (user.gender) {
            Gender.MALE -> when (user.activityLevel) {
                ActivityLevel.SEDENTARY -> 1.6
                ActivityLevel.LIGHT -> 1.7
                ActivityLevel.MODERATE -> 1.8
                ActivityLevel.ACTIVE -> 2.0
                ActivityLevel.VERY_ACTIVE -> 2.2
            }
            Gender.FEMALE -> when (user.activityLevel) {
                ActivityLevel.SEDENTARY -> 1.4
                ActivityLevel.LIGHT -> 1.5
                ActivityLevel.MODERATE -> 1.6
                ActivityLevel.ACTIVE -> 1.8
                ActivityLevel.VERY_ACTIVE -> 2.0
            }
        }

        val proteinGrams = user.weight * proteinMultiplier
        val fatCalories = ageAdjustedCalories * 0.25
        val fatGrams = fatCalories / 9
        val proteinCalories = proteinGrams * 4
        val carbCalories = ageAdjustedCalories - proteinCalories - fatCalories
        val carbGrams = carbCalories / 4

        return NutritionTarget(
            calories = createRange(ageAdjustedCalories.toInt(), 0.1),
            protein = createRange(proteinGrams.toInt(), 0.15),
            carbs = createRange(carbGrams.toInt(), 0.15),
            fat = createRange(fatGrams.toInt(), 0.15),
            sodium = 2300.0,
            fiber = calculateAgeBasedFiber(user.age),
            sugar = 50.0
        )
    }

    private fun createRange(baseValue: Int, tolerance: Double): Range {
        val baseDouble = baseValue.toDouble()
        val min = baseDouble * (1 - tolerance)
        val max = baseDouble * (1 + tolerance)

        return Range(
            min = min.coerceAtLeast(0.0), // 确保最小值不小于0
            max = max
        )
    }


    private fun calculateAgeBasedFiber(age: Int): Double {
        return when {
            age < 18 -> 25.0
            age in 18..50 -> 28.0
            else -> 22.0  // 老年人纤维需求略低
        }
    }
}