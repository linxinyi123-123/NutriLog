package com.example.nutrilog.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutrilog.ui.components.LineDataPoint
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class PerformanceTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testChartRenderingPerformance() {
        val testData = generateTestData(1000) // 生成1000个测试点
        
        val renderTime = measureTimeMillis {
            composeTestRule.setContent {
                OptimizedLineChart(
                    dataPoints = testData,
                    lineColor = androidx.compose.ui.graphics.Color.Blue,
                    fillColor = androidx.compose.ui.graphics.Color.Blue.copy(alpha = 0.1f),
                    showPoints = true,
                    showGrid = true,
                    onClick = { _ -> }
                )
            }
        }
        
        println("图表渲染时间: ${renderTime}ms")
        assertTrue(renderTime < 100, "图表应在100ms内完成渲染")
    }

    @Test
    fun testMemoryUsage() {
        val runtime = Runtime.getRuntime()
        
        // 强制垃圾回收
        runtime.gc()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()
        
        // 渲染复杂界面
        composeTestRule.setContent {
            // 创建一个包含多个图表的复杂界面
            androidx.compose.foundation.layout.Column {
                for (i in 0 until 5) {
                    val testData = generateTestData(500)
                    OptimizedLineChart(
                        dataPoints = testData,
                        lineColor = androidx.compose.ui.graphics.Color.Blue,
                        fillColor = androidx.compose.ui.graphics.Color.Blue.copy(alpha = 0.1f),
                        showPoints = true,
                        showGrid = true,
                        onClick = { _ -> }
                    )
                }
            }
        }
        
        // 再次强制垃圾回收
        runtime.gc()
        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryUsed = finalMemory - initialMemory
        
        println("内存使用: ${memoryUsed / 1024 / 1024}MB")
        assertTrue(memoryUsed < 50 * 1024 * 1024, "内存使用应小于50MB")
    }

    @Test
    fun testUIResponsiveness() {
        val testData = generateTestData(100)
        
        composeTestRule.setContent {
            OptimizedLineChart(
                dataPoints = testData,
                lineColor = androidx.compose.ui.graphics.Color.Blue,
                fillColor = androidx.compose.ui.graphics.Color.Blue.copy(alpha = 0.1f),
                showPoints = true,
                showGrid = true,
                onClick = { _ -> }
            )
        }
        
        val totalTime = measureTimeMillis {
            repeat(100) {
                // 模拟快速点击响应
                composeTestRule.onRoot().performClick()
            }
        }
        
        val averageTime = totalTime / 100f
        println("平均点击响应时间: ${averageTime}ms")
        assertTrue(averageTime < 50, "平均点击响应时间应小于50ms")
    }

    private fun generateTestData(count: Int): List<LineDataPoint> {
        return List(count) { index ->
            val value = Math.sin(index.toDouble() / 10) * 50 + 50
            LineDataPoint(
                label = "Day ${index + 1}",
                value = value.toFloat(),
                extraData = mapOf(
                    "calories" to (value * 2).toFloat(),
                    "protein" to (value * 0.1).toFloat()
                )
            )
        }
    }
}
