package com.example.nutrilog.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.nutrilog.shared.DailyAnalysis

@Composable
fun ResponsiveLayout(
    windowSize: WindowSizeClass,
    content: @Composable (WindowSizeClass) -> Unit
) {
    content(windowSize)
}

@Composable
fun calculateWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> WindowSizeClass.Compact
        screenWidth < 840.dp -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }
}

enum class WindowSizeClass {
    Compact,    // 手机竖屏
    Medium,     // 平板竖屏/手机横屏
    Expanded    // 平板横屏
}

@Composable
fun AdaptiveAnalysisScreen(
    windowSize: WindowSizeClass,
    analysis: DailyAnalysis
) {
    when (windowSize) {
        WindowSizeClass.Compact -> {
            // 手机竖屏：单列布局
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                com.example.nutrilog.ui.screens.HealthScoreOverview(score = analysis.score)
                Spacer(modifier = Modifier.height(24.dp))
                NutritionPieChart(nutrition = analysis.nutrition)
                Spacer(modifier = Modifier.height(24.dp))
                NutrientRadarChart(
                    actual = analysis.nutrition,
                    target = analysis.target
                )
                Spacer(modifier = Modifier.height(24.dp))
                // 添加食物类别分布柱状图
                val mockCategoryData = mapOf(
                    "谷薯类" to 30.0,
                    "蔬菜类" to 25.0,
                    "水果类" to 15.0,
                    "蛋白质类" to 20.0,
                    "奶制品" to 5.0,
                    "油脂类" to 5.0
                )
                CategoryBarChart(varietyData = mockCategoryData)
                Spacer(modifier = Modifier.height(24.dp))
                // 添加改进建议
                com.example.nutrilog.ui.screens.ImprovementSuggestions(suggestions = analysis.score.feedback)
            }
        }
        
        WindowSizeClass.Medium -> {
            // 平板竖屏：两列布局
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    com.example.nutrilog.ui.screens.HealthScoreOverview(score = analysis.score)
                    Spacer(modifier = Modifier.height(24.dp))
                    NutritionPieChart(nutrition = analysis.nutrition)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    NutrientRadarChart(
                        actual = analysis.nutrition,
                        target = analysis.target
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // 添加食物类别分布柱状图
                    val mockCategoryData = mapOf(
                        "谷薯类" to 30.0,
                        "蔬菜类" to 25.0,
                        "水果类" to 15.0,
                        "蛋白质类" to 20.0,
                        "奶制品" to 5.0,
                        "油脂类" to 5.0
                    )
                    CategoryBarChart(varietyData = mockCategoryData)
                    Spacer(modifier = Modifier.height(24.dp))
                    // 添加改进建议
                    com.example.nutrilog.ui.screens.ImprovementSuggestions(suggestions = analysis.score.feedback)
                }
            }
        }
        
        WindowSizeClass.Expanded -> {
            // 平板横屏：网格布局
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    com.example.nutrilog.ui.screens.HealthScoreOverview(score = analysis.score)
                }
                
                item {
                    NutritionPieChart(nutrition = analysis.nutrition)
                }
                
                item {
                    NutrientRadarChart(
                        actual = analysis.nutrition,
                        target = analysis.target
                    )
                }
                
                item {
                    // 添加食物类别分布柱状图
                    val mockCategoryData = mapOf(
                        "谷薯类" to 30.0,
                        "蔬菜类" to 25.0,
                        "水果类" to 15.0,
                        "蛋白质类" to 20.0,
                        "奶制品" to 5.0,
                        "油脂类" to 5.0
                    )
                    CategoryBarChart(varietyData = mockCategoryData)
                }
                
                item(span = { GridItemSpan(2) }) {
                    // 添加改进建议
                    com.example.nutrilog.ui.screens.ImprovementSuggestions(suggestions = analysis.score.feedback)
                }
            }
        }
    }
}