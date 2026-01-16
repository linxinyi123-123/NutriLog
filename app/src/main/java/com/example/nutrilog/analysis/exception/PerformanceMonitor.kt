package com.example.nutrilog.analysis.exception

import android.util.Log

class PerformanceMonitor {
    private val executionTimes = mutableMapOf<String, MutableList<Long>>()

    suspend fun <T> measure(
        operationName: String,
        block: suspend () -> T
    ): T {
        val startTime = System.currentTimeMillis()
        try {
            return block()
        } finally {
            val duration = System.currentTimeMillis() - startTime
            recordDuration(operationName, duration)

            // 如果操作过慢，记录警告
            if (duration > 1000) {
                Log.w("Performance", "$operationName took ${duration}ms")
            }
        }
    }

    private fun recordDuration(operationName: String, duration: Long) {
        val times = executionTimes.getOrPut(operationName) { mutableListOf() }
        times.add(duration)
        
        // 限制记录数量，避免内存泄漏
        if (times.size > 100) {
            times.removeAt(0)
        }
    }

    fun getStats(operationName: String): PerformanceStats {
        val times = executionTimes[operationName] ?: emptyList()
        return PerformanceStats(
            count = times.size,
            average = times.average(),
            max = times.maxOrNull() ?: 0,
            min = times.minOrNull() ?: 0
        )
    }
}

data class PerformanceStats(
    val count: Int,
    val average: Double,
    val max: Long,
    val min: Long
)