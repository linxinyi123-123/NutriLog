package com.example.nutrilog.analysis.exception

class CalculationException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    constructor() : this("Calculation failed")
}