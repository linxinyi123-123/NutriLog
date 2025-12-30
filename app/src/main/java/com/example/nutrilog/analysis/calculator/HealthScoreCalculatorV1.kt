package com.example.nutrilog.analysis.calculator

import com.example.nutrilog.shared.HealthScore
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.NutritionTarget
import com.example.nutrilog.shared.Range
import kotlin.math.max
import kotlin.math.min

class HealthScoreCalculatorV1(private val target: NutritionTarget) {
    fun calculateScore(actual: NutritionFacts): HealthScore {
        var score = 100.0
        val feedback = mutableListOf<String>()

        // 1. 热量评分（40分）
        val calorieScore = calculateCalorieScore(actual.calories)
        score = score * 0.6 + calorieScore * 0.4  // 调整权重分配

        // 2. 三大营养素比例评分（40分）
        val macroScore = calculateMacroScore(actual)
        score = score * 0.6 + macroScore * 0.4  // 调整权重分配

        // 3. 微量营养素评分（20分）
        val microScore = calculateMicroScore(actual, feedback) // 传入feedback收集器
        score = score * 0.8 + microScore * 0.2


        return HealthScore(
            total = score.coerceIn(0.0, 100.0),  // 上限保持100分
            breakdown = mapOf(
                "calories" to calorieScore,
                "macros" to macroScore,
                "micros" to microScore
            ),
            feedback = feedback
        )
    }

    private fun calculateCalorieScore(calories: Double): Double {
        return when {
            calories < target.calories.min * 0.8 -> 40.0  // 严重不足
            calories < target.calories.min -> 60.0         // 不足
            calories <= target.calories.max -> 100.0       // 理想
            calories <= target.calories.max * 1.2 -> 70.0  // 略超
            else -> 30.0                                   // 严重超标
        }
    }

    private fun calculateMacroScore(actual: NutritionFacts): Double {
        // 1. 单项营养素评分
        val proteinScore = evaluateNutrientScore(actual.protein, target.protein)
        val carbsScore = evaluateNutrientScore(actual.carbs, target.carbs)
        val fatScore = evaluateNutrientScore(actual.fat, target.fat)

        // 2. 营养素比例平衡评分（额外考量）
        val balanceScore = calculateBalanceScore(actual)

        // 3. 加权平均计算总分
        // 蛋白质权重稍高（40%），碳水35%，脂肪25%）
        val weightedScore = proteinScore * 0.4 + carbsScore * 0.35 + fatScore * 0.25

        // 平衡性作为加成因子（±10分）
        val balanceAdjustment = (balanceScore - 50) * 0.2 // 将平衡分数转换为调整值

        return (weightedScore + balanceAdjustment).coerceIn(0.0, 100.0)
    }

    private fun evaluateNutrientScore(actual: Double, target: Range): Double {
        return when {
            actual < target.min * 0.8 -> 40.0
            actual < target.min -> 60.0
            actual <= target.max -> 100.0
            actual <= target.max * 1.2 -> 70.0
            else -> 30.0
        }
    }

    private fun calculateBalanceScore(actual: NutritionFacts): Double {
        val totalCalories = actual.calories
        if (totalCalories <= 0) return 50.0 // 避免除零

        // 计算各营养素提供的热量占比
        val proteinCalories = actual.protein * 4
        val carbsCalories = actual.carbs * 4
        val fatCalories = actual.fat * 9

        val proteinRatio = proteinCalories / totalCalories
        val carbsRatio = carbsCalories / totalCalories
        val fatRatio = fatCalories / totalCalories

        // 理想比例：蛋白质20-30%，碳水45-65%，脂肪20-35%
        val idealProteinMin = 0.20
        val idealProteinMax = 0.30
        val idealCarbsMin = 0.45
        val idealCarbsMax = 0.65
        val idealFatMin = 0.20
        val idealFatMax = 0.35

        // 计算每个营养素的偏差
        val proteinDeviation = when {
            proteinRatio < idealProteinMin -> (idealProteinMin - proteinRatio) / idealProteinMin
            proteinRatio > idealProteinMax -> (proteinRatio - idealProteinMax) / idealProteinMax
            else -> 0.0
        }

        val carbsDeviation = when {
            carbsRatio < idealCarbsMin -> (idealCarbsMin - carbsRatio) / idealCarbsMin
            carbsRatio > idealCarbsMax -> (carbsRatio - idealCarbsMax) / idealCarbsMax
            else -> 0.0
        }

        val fatDeviation = when {
            fatRatio < idealFatMin -> (idealFatMin - fatRatio) / idealFatMin
            fatRatio > idealFatMax -> (fatRatio - idealFatMax) / idealFatMax
            else -> 0.0
        }

        // 平均偏差越小，平衡分数越高
        val avgDeviation = (proteinDeviation + carbsDeviation + fatDeviation) / 3.0
        return (100 - avgDeviation * 100).coerceIn(0.0, 100.0)
    }

    private fun calculateMicroScore(actual: NutritionFacts, feedback: MutableList<String>): Double {
        var totalScore = 0.0

        // 1. 钠评分（考虑高血压风险）
        val sodiumScore = evaluateSodiumScore(actual.sodium, target.sodium)
        totalScore += sodiumScore * 0.3

        if (sodiumScore < 70) {
            feedback.add("钠摄入过高，建议减少加工食品")
        }

        // 2. 膳食纤维评分（考虑年龄和性别差异）
        val fiberScore = evaluateFiberScore(actual.fiber, target.fiber, actual.calories)
        totalScore += fiberScore * 0.4

        if (fiberScore < 70) {
            feedback.add("膳食纤维不足，建议增加蔬菜水果摄入")
        }

        // 3. 添加糖评分（考虑心血管疾病风险）
        val sugarScore = evaluateSugarScore(actual.sugar, target.sugar, actual.calories)
        totalScore += sugarScore * 0.3

        if (sugarScore < 70) {
            feedback.add("添加糖摄入过多，建议减少甜食饮料")
        }

        return totalScore
    }

    private fun evaluateSodiumScore(actualSodium: Double?, targetSodium: Double): Double {
        if (actualSodium != null) {
            return when {
                actualSodium <= targetSodium -> 100.0                    // 优秀
                actualSodium <= targetSodium * 1.2 -> 80.0              // 良好
                actualSodium <= targetSodium * 1.5 -> 60.0              // 一般
                actualSodium <= targetSodium * 2.0 -> 40.0              // 较差
                else -> 20.0                                            // 很差
            }
        }
        return 0.0
    }

    private fun evaluateFiberScore(actualFiber: Double?, targetFiber: Double, totalCalories: Double): Double {
        // 根据热量调整纤维需求（每1000卡路里需要约14g纤维）
        val calorieAdjustedTarget = (totalCalories / 1000) * 14
        val effectiveTarget = maxOf(targetFiber, calorieAdjustedTarget)

        if (actualFiber != null) {
            return when {
                actualFiber >= effectiveTarget * 1.2 -> 100.0           // 优秀
                actualFiber >= effectiveTarget -> 90.0                  // 良好
                actualFiber >= effectiveTarget * 0.8 -> 70.0             // 一般
                actualFiber >= effectiveTarget * 0.5 -> 50.0             // 较差
                else -> 30.0                                            // 很差
            }
        }
        return 0.0
    }

    private fun evaluateSugarScore(actualSugar: Double?, targetSugar: Double, totalCalories: Double): Double {
        // 根据热量调整糖限制（WHO建议不超过总热量的10%）
        val calorieBasedSugarLimit = totalCalories * 0.1 / 4  // 转换为克数（1g糖=4kcal）
        val effectiveTarget = minOf(targetSugar, calorieBasedSugarLimit)

        if (actualSugar != null) {
            return when {
                actualSugar <= effectiveTarget * 0.5 -> 100.0          // 优秀
                actualSugar <= effectiveTarget -> 85.0                 // 良好
                actualSugar <= effectiveTarget * 1.2 -> 60.0             // 一般
                actualSugar <= effectiveTarget * 1.5 -> 40.0             // 较差
                else -> 20.0                                            // 很差
            }
        }
        return 0.0
    }
}