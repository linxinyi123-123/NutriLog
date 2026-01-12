package com.example.nutrilog.performance

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.nutrilog.ui.components.LineDataPoint
import com.example.nutrilog.ui.components.OptimizedLineChart
import com.example.nutrilog.ui.screens.AnalysisScreen
import com.example.nutrilog.ui.theme.AppColors
import org.junit.Rule
import org.junit.Test
import java.util.Random
import org.junit.Assert.assertTrue

/**
 * 性能测试套件
 */
class PerformanceTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private val random = Random()
    
    /**
     * 测试图表渲染性能
     * 生成1000个测试点，确保在100ms内完成渲染
     */
    @Test
    fun testChartRenderingPerformance() {
        val testData = generateTestData(1000)
        
        val startTime = System.currentTimeMillis()
        
        composeTestRule.setContent {
            OptimizedLineChart(
                dataPoints = testData,
                lineColor = AppColors.Primary,
                fillColor = AppColors.Primary.copy(alpha = 0.1f),
                showPoints = true,
                showGrid = true,
                onClick = { /* 空实现 */ }
            )
        }
        
        val renderTime = System.currentTimeMillis() - startTime
        println("图表渲染时间: ${renderTime}ms")
        org.junit.Assert.assertTrue("图表应在100ms内完成渲染", renderTime < 100)
    }
    
    /**
     * 测试内存使用
     * 渲染复杂界面，确保内存使用小于50MB
     */
    @Test
    fun testMemoryUsage() {
        val runtime = Runtime.getRuntime()
        
        // 强制垃圾回收
        runtime.gc()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()
        
        // 渲染复杂界面
        composeTestRule.setContent {
            AnalysisScreen(navController = rememberNavController())
        }
        
        // 再次强制垃圾回收
        runtime.gc()
        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryUsed = finalMemory - initialMemory
        
        println("内存使用: ${memoryUsed / 1024 / 1024}MB")
        org.junit.Assert.assertTrue("内存使用应小于50MB", memoryUsed < 50 * 1024 * 1024)
    }
    
    /**
     * 生成测试数据点
     */
    private fun generateTestData(count: Int): List<LineDataPoint> {
        return List(count) { index ->
            LineDataPoint(
                label = "$index",
                value = random.nextFloat() * 100f,
                extraData = mapOf(
                    "calories" to random.nextFloat() * 2000f,
                    "protein" to random.nextFloat() * 100f
                )
            )
        }
    }
}
