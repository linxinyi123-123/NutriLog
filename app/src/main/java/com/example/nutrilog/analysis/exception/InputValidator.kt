package com.example.nutrilog.analysis.exception

import com.example.nutrilog.shared.NutritionFacts
import java.time.LocalDate

class InputValidator {
    companion object {
        fun validateNutritionFacts(facts: NutritionFacts): ValidationResult {
            val errors = mutableListOf<String>()

            if (facts.calories < 0) errors.add("热量不能为负值")
            if (facts.calories > 10000) errors.add("热量值异常高")
            if (facts.protein < 0) errors.add("蛋白质不能为负值")
            if (facts.protein > 500) errors.add("蛋白质值异常高")

            return if (errors.isEmpty()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(errors)
            }
        }

        fun validateDate(date: String): Boolean {
            return try {
                LocalDate.parse(date)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<String>) : ValidationResult()
}