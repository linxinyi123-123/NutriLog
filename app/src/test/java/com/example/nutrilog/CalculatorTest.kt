package com.example.nutrilog

import com.example.nutrilog.analysis.calculator.BasicNutritionCalculator
import com.example.nutrilog.shared.FoodItem
import kotlin.test.Test
import kotlin.test.assertEquals


class CalculatorTest{
    @Test
    fun testCalculateFoodNutrition() {
        val rice = FoodItem(1, "米饭", "主食", 116.0, 2.6, 25.9, 0.3)
        val calculator = BasicNutritionCalculator()
        val result = calculator.calculateFoodNutrition(rice, 200.0)

        assertEquals(232.0, result.calories)  // 116 * 2
        assertEquals(5.2, result.protein)     // 2.6 * 2
    }
}
