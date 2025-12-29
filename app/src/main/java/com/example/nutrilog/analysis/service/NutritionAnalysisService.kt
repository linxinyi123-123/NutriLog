package com.example.nutrilog.analysis.service

import com.example.nutrilog.analysis.calculator.BasicNutritionCalculator
import com.example.nutrilog.analysis.calculator.HealthScoreCalculatorV1
import com.example.nutrilog.analysis.calculator.PersonalizedTargetCalculator
import com.example.nutrilog.analysis.repository.AnalysisRepository
import com.example.nutrilog.shared.DailyAnalysis
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.NutritionTarget
import com.example.nutrilog.shared.NutritionTargetFactory

class NutritionAnalysisService(
    private val repository: AnalysisRepository,
    private val calculator: BasicNutritionCalculator,
    private val scoreCalculator: HealthScoreCalculatorV1
) {
    suspend fun analyzeDay(date: String): DailyAnalysis {
        // 1. 获取记录
        val records = repository.getRecordsByDate(date)

        // 2. 计算营养
        val nutrition = calculator.calculateDailyNutrition(records)

        // 3. 获取用户目标
        val user = repository.getUserProfile()
        val target = if (user != null) {
            PersonalizedTargetCalculator().calculateTargets(user)
        } else {
            NutritionTargetFactory().createForAdultMale()
        }

        // 4. 计算健康评分
        val score = scoreCalculator.calculateScore(nutrition)

        val newnutrition = com.example.nutrilog.shared.NutritionFacts(
            calories = nutrition.calories,
            protein = nutrition.protein,
            carbs = nutrition.carbs,
            fat = nutrition.fat,
            fiber = nutrition.fiber,
            sugar = nutrition.sugar,
            sodium = nutrition.sodium
        )

        val newtarget = NutritionFacts(
            calories = (target.calories.min+target.calories.max)/2,
            protein = (target.protein.min+target.protein.max)/2,
            carbs = (target.carbs.min+target.protein.max)/2,
            fat = (target.fat.min+target.fat.max)/2,
            fiber = target.fiber,
            sugar = target.sugar,
            sodium = target.sodium
        )

        return DailyAnalysis(
            date = date,
            nutrition = newnutrition,
            target = newtarget,
            score = score,
            records = records
        )
    }
}