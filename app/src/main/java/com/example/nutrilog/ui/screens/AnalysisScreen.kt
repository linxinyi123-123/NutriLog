package com.example.nutrilog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrilog.shared.*
import com.example.nutrilog.ui.theme.AppColors
import com.example.nutrilog.ui.theme.AppShapes
import com.example.nutrilog.ui.theme.AppTypography

// 分析模块 ViewModel（临时实现）
class AnalysisViewModel {
    var selectedDate by mutableStateOf(getTodayDate())
    var currentAnalysis by mutableStateOf<DailyAnalysis?>(getMockDailyAnalysis())
    var isLoading by mutableStateOf(false)
    
    fun selectDate(date: String) {
        selectedDate = date
        // 这里应该加载对应日期的分析数据
        currentAnalysis = getMockDailyAnalysis()
    }
    
    fun refresh() {
        isLoading = true
        // 这里应该重新加载数据
        currentAnalysis = getMockDailyAnalysis()
        isLoading = false
    }
    
    private fun getTodayDate(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }
    
    private fun getMockDailyAnalysis(): DailyAnalysis {
        return DailyAnalysis(
            date = getTodayDate(),
            score = HealthScore(
                total = 85.5,
                breakdown = mapOf(
                    "calories" to 90.0,
                    "macros" to 85.0,
                    "micros" to 78.0,
                    "regularity" to 92.0,
                    "variety" to 88.0
                ),
                feedback = listOf(
                    "蛋白质摄入量充足，继续保持！",
                    "碳水化合物比例适中，建议增加一些膳食纤维。",
                    "脂肪摄入量控制良好。"
                )
            ),
            nutrition = NutritionFacts(
                calories = 1850.0,
                protein = 85.5,
                carbs = 220.0,
                fat = 58.0,
                fiber = 25.0,
                sugar = 45.0
            ),
            target = NutritionFacts(
                calories = 2000.0,
                protein = 75.0,
                carbs = 250.0,
                fat = 65.0,
                fiber = 30.0,
                sugar = 50.0
            ),
            records = emptyList()
        )
    }
}

// 分析主界面
@Composable
fun AnalysisScreen(navController: NavController) {
    val viewModel = remember { AnalysisViewModel() }
    val datePickerState = rememberDatePickerState()
    
    Scaffold(
        topBar = {
            AnalysisTopBar(
                selectedDate = viewModel.selectedDate,
                onDateChange = { viewModel.selectDate(it) },
                onCalendarClick = { /* 日历点击逻辑，暂时未实现 */ }
            )
        }
    ) {
        AnalysisContent(
            modifier = Modifier.padding(it),
            analysis = viewModel.currentAnalysis,
            isLoading = viewModel.isLoading,
            onRefresh = { viewModel.refresh() }
        )
    }
}

// 分析顶部栏
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisTopBar(
    selectedDate: String,
    onDateChange: (String) -> Unit,
    onCalendarClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "营养分析",
                    style = AppTypography.h2,
                    color = AppColors.OnSurface
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // 日期选择器
                DateSelector(
                    selectedDate = selectedDate,
                    onDateChange = onDateChange,
                    onCalendarClick = onCalendarClick
                )
            }
        },
        actions = {
            IconButton(onClick = { /* 分享功能 */ }) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Share,
                    contentDescription = "分享报告",
                    tint = AppColors.OnSurface
                )
            }
            IconButton(onClick = { /* 导出功能 */ }) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Download,
                    contentDescription = "导出数据",
                    tint = AppColors.OnSurface
                )
            }
        }
    )
}

// 日期选择器组件
@Composable
fun DateSelector(
    selectedDate: String,
    onDateChange: (String) -> Unit,
    onCalendarClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable { onCalendarClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedDate,
            style = AppTypography.body1,
            color = AppColors.OnSurfaceVariant
        )
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.CalendarMonth,
            contentDescription = "选择日期",
            modifier = Modifier.size(16.dp),
            tint = AppColors.OnSurfaceVariant
        )
    }
}

// 分析内容区域
@Composable
fun AnalysisContent(
    modifier: Modifier = Modifier,
    analysis: DailyAnalysis?,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            LoadingView()
        } else if (analysis == null) {
            EmptyAnalysisView(onRefresh = onRefresh)
        } else {
            AnalysisDetailView(analysis = analysis)
        }
    }
}

// 加载视图
@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = AppColors.Primary)
    }
}

// 空分析视图
@Composable
fun EmptyAnalysisView(onRefresh: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "暂无分析数据",
                style = AppTypography.h2,
                color = AppColors.OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRefresh) {
                Text(text = "刷新数据")
            }
        }
    }
}

// 分析详情视图
@Composable
fun AnalysisDetailView(analysis: DailyAnalysis) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 1. 健康评分概览
        HealthScoreOverview(score = analysis.score)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 2. 营养环形图
        com.example.nutrilog.ui.components.NutritionPieChart(nutrition = analysis.nutrition)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 3. 营养素达成雷达图
        com.example.nutrilog.ui.components.NutrientRadarChart(
            actual = analysis.nutrition,
            target = analysis.target
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 4. 食物类别分布柱状图
        val mockCategoryData = mapOf(
            "谷薯类" to 30.0,
            "蔬菜类" to 25.0,
            "水果类" to 15.0,
            "蛋白质类" to 20.0,
            "奶制品" to 5.0,
            "油脂类" to 5.0
        )
        com.example.nutrilog.ui.components.CategoryBarChart(varietyData = mockCategoryData)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 5. 改进建议
        ImprovementSuggestions(suggestions = analysis.score.feedback)
    }
}

// 占位符视图
@Composable
fun PlaceholderView(title: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.large,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = AppTypography.body1,
                color = AppColors.OnSurfaceVariant
            )
        }
    }
}

// 改进建议组件
@Composable
fun ImprovementSuggestions(suggestions: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.large,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "改进建议",
                style = AppTypography.h2,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            suggestions.forEachIndexed { index, suggestion ->
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1}. ",
                        style = AppTypography.body1,
                        color = AppColors.Primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = suggestion,
                        style = AppTypography.body1,
                        color = AppColors.OnSurface
                    )
                }
            }
        }
    }
}

// 健康评分概览组件
@Composable
fun HealthScoreOverview(score: HealthScore) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 总分显示
            Text(
                text = "今日健康评分",
                style = AppTypography.h2,
                color = AppColors.OnSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 大号分数显示
            Text(
                text = "${score.total.toInt()}",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 64.sp
                ),
                color = getScoreColor(score.total.toInt())
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 分数描述
            Text(
                text = getScoreDescription(score.total.toInt()),
                style = AppTypography.body1,
                color = AppColors.OnSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 各维度分数
            ScoreBreakdown(breakdown = score.breakdown)
        }
    }
}

// 分数分解组件
@Composable
fun ScoreBreakdown(breakdown: Map<String, Double>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        breakdown.forEach { (dimension, score) ->
            ScoreBreakdownItem(
                dimension = dimension,
                score = score,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// 分数分解项组件
@Composable
fun ScoreBreakdownItem(
    dimension: String,
    score: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getDimensionDisplayName(dimension),
            style = AppTypography.body1,
            color = AppColors.OnSurface
        )
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 分数
            Text(
                text = score.toInt().toString(),
                style = AppTypography.body1.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                color = getScoreColor(score.toInt())
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 迷你进度条
            Box(
            modifier = Modifier
                .width(100.dp)
                .height(6.dp)
                .clip(shape = RoundedCornerShape(3.dp))
                .background(AppColors.Background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(((score / 100.0).coerceIn(0.0, 1.0) * 100.0).dp)
                    .clip(shape = RoundedCornerShape(3.dp))
                    .background(getScoreColor(score.toInt()))
            )
        }
        }
    }
}

// 辅助函数：获取维度显示名称
fun getDimensionDisplayName(dimension: String): String {
    return when (dimension) {
        "calories" -> "热量平衡"
        "macros" -> "营养素比例"
        "micros" -> "微量元素"
        "regularity" -> "饮食规律"
        "variety" -> "食物多样性"
        else -> dimension
    }
}

// 辅助函数：获取分数描述
fun getScoreDescription(score: Int): String {
    return when {
        score < 60 -> "需要显著改善"
        score < 70 -> "有改进空间"
        score < 80 -> "表现良好"
        score < 90 -> "非常优秀"
        else -> "完美表现"
    }
}

// 辅助函数：获取分数颜色
@Composable
fun getScoreColor(score: Int): androidx.compose.ui.graphics.Color {
    return when {
        score < 60 -> AppColors.Error
        score < 80 -> AppColors.Warning
        else -> AppColors.Success
    }
}

// 日期选择器状态（简化实现）
@Composable
fun rememberDatePickerState(): DatePickerState {
    return remember {
        DatePickerState(false)
    }
}

// 日期选择器状态类（简化实现）
class DatePickerState(
    var isVisible: Boolean
) {
    fun show() {
        isVisible = true
    }
    
    fun dismiss() {
        isVisible = false
    }
}
