package com.example.nutrilog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import com.example.nutrilog.shared.*
import com.example.nutrilog.ui.theme.AppColors
import com.example.nutrilog.ui.theme.AppShapes
import com.example.nutrilog.ui.theme.AppTypography
import com.example.nutrilog.features.recommendation.model.Recommendation
import com.example.nutrilog.features.recommendation.model.RecommendationType
import com.example.nutrilog.features.recommendation.model.Priority
import com.example.nutrilog.features.recommendation.model.GoalType
import com.example.nutrilog.features.recommendation.model.improvement.ImprovementPlan
import com.example.nutrilog.features.recommendation.model.improvement.WeeklyPlan
import com.example.nutrilog.features.recommendation.model.improvement.WeeklyTargets
import com.example.nutrilog.features.recommendation.model.improvement.DailyTask
import com.example.nutrilog.features.recommendation.model.improvement.TaskType
import com.example.nutrilog.features.recommendation.model.improvement.Milestone
import java.time.LocalDate

// 分析模块状态密封类
sealed class AnalysisState {
    object Loading : AnalysisState()
    data class Success(val analysis: DailyAnalysis) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
    object Empty : AnalysisState()
}

// 分析模块 ViewModel
class AnalysisViewModel : ViewModel() {
    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Loading)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()
    
    init {
        // 初始加载今天的数据
        viewModelScope.launch {
            getAnalysisForDate(getTodayDate()).collect {
                _analysisState.value = it
            }
        }
    }
    
    fun getAnalysisForDate(date: String): Flow<AnalysisState> {
        return flow {
            emit(AnalysisState.Loading)
            try {
                // 模拟网络请求延迟
                delay(1000)
                
                // 这里应该调用真实的分析服务
                val analysis = getMockDailyAnalysis(date)
                
                if (analysis.records.isEmpty()) {
                    emit(AnalysisState.Empty)
                } else {
                    emit(AnalysisState.Success(analysis))
                }
            } catch (e: Exception) {
                emit(AnalysisState.Error(e.message ?: "未知错误"))
            }
        }
    }
    
    fun refreshAnalysis(date: String) {
        viewModelScope.launch {
            getAnalysisForDate(date).collect {
                _analysisState.value = it
            }
        }
    }
    
    fun getTodayDate(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }
    
    private fun getMockDailyAnalysis(date: String): DailyAnalysis {
        return DailyAnalysis(
            date = date,
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
                sugar = 45.0,
            ),
            target = NutritionFacts(
                calories = 2000.0,
                protein = 75.0,
                carbs = 250.0,
                fat = 65.0,
                fiber = 30.0,
                sugar = 50.0,
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
                selectedDate = viewModel.getTodayDate(),
                onDateChange = { viewModel.getAnalysisForDate(it) },
                onCalendarClick = { /* 日历点击逻辑，暂时未实现 */ }
            )
        }
    ) {
        AnalysisContent(
            modifier = Modifier.padding(it),
            analysis = (viewModel.analysisState.collectAsState().value as? AnalysisState.Success)?.analysis,
            isLoading = viewModel.analysisState.collectAsState().value is AnalysisState.Loading,
            onRefresh = { viewModel.refreshAnalysis(viewModel.getTodayDate()) }
        )
    }
}

// 带数据集成的分析屏幕
@Composable
fun AnalysisScreenWithData(
    date: String,
    viewModel: AnalysisViewModel = viewModel()
) {
    val analysisState by viewModel.getAnalysisForDate(date).collectAsState(initial = AnalysisState.Loading)
    
    when (analysisState) {
        is AnalysisState.Loading -> {
            LoadingView(message = "正在分析营养数据...")
        }
        
        is AnalysisState.Success -> {
            val analysis = (analysisState as AnalysisState.Success).analysis
            AnalysisDetailView(analysis = analysis)
        }
        
        is AnalysisState.Error -> {
            ErrorView(
                message = "分析数据时出错",
                onRetry = { viewModel.refreshAnalysis(date) }
            )
        }
        
        is AnalysisState.Empty -> {
            EmptyState(
                icon = Icons.Default.Assessment,
                title = "暂无分析数据",
                message = "请先记录饮食以生成分析报告"
            )
        }
    }
}

// 错误视图
@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = AppTypography.body1,
                color = AppColors.Error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(onClick = onRetry) {
                Text(text = "重试")
            }
        }
    }
}

// 空状态视图
@Composable
fun EmptyState(icon: ImageVector, title: String, message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.OnSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = AppTypography.h2,
                color = AppColors.OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = AppTypography.body1,
                color = AppColors.OnSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

// 自动刷新分析数据组合函数
@Composable
fun AutoRefreshAnalysis(
    date: String,
    onDataUpdated: (DailyAnalysis) -> Unit
) {
    // 使用LaunchedEffect监听date变化
    LaunchedEffect(date) {
        // 模拟网络请求延迟
        delay(500)
        // 使用mock数据进行测试
        val mockAnalysis = DailyAnalysis(
            date = date,
            nutrition = NutritionFacts(
                calories = 1850.0,
                protein = 85.5,
                carbs = 220.0,
                fat = 58.0,
                fiber = 25.0,
                sugar = 45.0,
            ),
            target = NutritionFacts(
                calories = 2000.0,
                protein = 75.0,
                carbs = 250.0,
                fat = 65.0,
                fiber = 30.0,
                sugar = 50.0,
            ),
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
            records = emptyList()
        )
        onDataUpdated(mockAnalysis)
    }
}

// 加载视图
@Composable
fun LoadingView(message: String = "正在加载...") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = AppColors.Primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = AppTypography.body1,
                color = AppColors.OnSurfaceVariant
            )
        }
    }
}
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
    // 模拟获取推荐数据
    val recommendations = getMockRecommendations()
    
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
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 6. 个性化推荐
        RecommendationsSection(
            recommendations = recommendations,
            onRecommendationClick = { recommendation ->
                // 推荐点击处理逻辑
                println("Recommendation clicked: ${recommendation.title}")
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 7. 改善计划
        Text(
            text = "改善计划",
            style = AppTypography.h2,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        ImprovementPlanView(plan = getMockImprovementPlan())
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

// 推荐相关辅助函数

// 根据优先级获取颜色
@Composable
fun getPriorityColor(priority: Priority): Color {
    return when (priority) {
        Priority.HIGH -> AppColors.Error
        Priority.MEDIUM -> AppColors.Warning
        Priority.LOW -> AppColors.Success
    }
}

// 根据推荐类型获取图标
@Composable
fun getRecommendationIcon(type: RecommendationType): ImageVector {
    return when (type) {
        RecommendationType.NUTRITION_GAP -> Icons.Default.Warning
        RecommendationType.MEAL_PLAN -> Icons.Default.CheckCircle
        RecommendationType.FOOD_SUGGESTION -> Icons.Default.Star
        RecommendationType.HABIT_IMPROVEMENT -> Icons.Default.Info
        RecommendationType.EDUCATIONAL -> Icons.Default.Info
    }
}

// 优先级标签组件
@Composable
fun PriorityBadge(priority: Priority) {
    val color = getPriorityColor(priority)
    val text = when (priority) {
        Priority.HIGH -> "高"
        Priority.MEDIUM -> "中"
        Priority.LOW -> "低"
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = AppTypography.body1.copy(fontSize = 12.sp),
            color = color
        )
    }
}

// 推荐卡片组件
@Composable
fun RecommendationCard(
    recommendation: Recommendation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = AppShapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getPriorityColor(recommendation.priority).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getRecommendationIcon(recommendation.type),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = getPriorityColor(recommendation.priority)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 内容
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recommendation.title,
                    style = AppTypography.h2.copy(fontSize = 16.sp),
                    color = AppColors.OnSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = recommendation.description,
                    style = AppTypography.body1,
                    color = AppColors.OnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 优先级标签
            PriorityBadge(priority = recommendation.priority)
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 箭头
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "查看详情",
                tint = AppColors.OnSurfaceVariant
            )
        }
    }
}

// 推荐列表组件
@Composable
fun RecommendationsSection(
    recommendations: List<Recommendation>,
    onRecommendationClick: (Recommendation) -> Unit
) {
    Column {
        Text(
            text = "个性化建议",
            style = AppTypography.h2,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recommendations) {
                recommendation ->
                RecommendationCard(
                    recommendation = recommendation,
                    onClick = { onRecommendationClick(recommendation) }
                )
            }
        }
    }
}

// 模拟获取推荐数据的函数
fun getMockRecommendations(): List<Recommendation> {
    return listOf(
        Recommendation(
            id = 1,
            type = RecommendationType.NUTRITION_GAP,
            title = "膳食纤维摄入不足",
            description = "您今天的膳食纤维摄入量仅为目标的60%，建议增加蔬菜和水果的摄入。",
            priority = Priority.HIGH,
            confidence = 0.9f,
            reason = "nutrition_gap_fiber",
            actions = emptyList()
        ),
        Recommendation(
            id = 2,
            type = RecommendationType.FOOD_SUGGESTION,
            title = "推荐食物",
            description = "基于您的营养需求，建议您多食用燕麦和菠菜来补充膳食纤维。",
            priority = Priority.MEDIUM,
            confidence = 0.85f,
            reason = "food_suggestion_fiber",
            actions = emptyList()
        ),
        Recommendation(
            id = 3,
            type = RecommendationType.HABIT_IMPROVEMENT,
            title = "饮食习惯建议",
            description = "建议您在早餐中添加一份水果，有助于维持血糖稳定和提高饱腹感。",
            priority = Priority.LOW,
            confidence = 0.75f,
            reason = "habit_improvement_breakfast",
            actions = emptyList()
        )
    )
}

// 改善计划相关辅助函数

// 根据进度获取颜色
@Composable
fun getProgressColor(progress: Int): Color {
    return when {
        progress < 30 -> AppColors.Error
        progress < 70 -> AppColors.Warning
        else -> AppColors.Success
    }
}

// 任务项组件
@Composable
fun TaskItem(task: com.example.nutrilog.features.recommendation.model.improvement.DailyTask) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.completed,
            onCheckedChange = { /* 更新任务状态 */ },
            colors = CheckboxDefaults.colors(
                checkedColor = AppColors.Primary
            )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = AppTypography.body1.copy(
                    textDecoration = if (task.completed) TextDecoration.LineThrough 
                                    else TextDecoration.None
                ),
                color = if (task.completed) AppColors.OnSurfaceVariant 
                       else AppColors.OnSurface
            )
            
            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    style = AppTypography.body1,
                    color = AppColors.OnSurfaceVariant
                )
            }
        }
    }
}

// 下一个里程碑卡片组件
@Composable
fun NextMilestoneCard(milestone: com.example.nutrilog.features.recommendation.model.improvement.Milestone) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = AppShapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "下一个里程碑",
                style = AppTypography.h2.copy(fontSize = 16.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                    text = milestone.title,
                    style = AppTypography.body1.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = milestone.description,
                    style = AppTypography.body1,
                    color = AppColors.OnSurfaceVariant
                )
            if (milestone.rewardPoints > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "奖励积分",
                        modifier = Modifier.size(16.dp),
                        tint = AppColors.Warning
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "完成奖励 ${milestone.rewardPoints} 积分",
                        style = AppTypography.body1,
                        color = AppColors.Warning
                    )
                }
            }
        }
    }
}

// 模拟获取改善计划数据
fun getMockImprovementPlan(): ImprovementPlan {
    val today = LocalDate.now()
    return ImprovementPlan(
        id = "1",
        userId = 1L,
        title = "膳食纤维改善计划",
        goalType = GoalType.NUTRIENT_BALANCE,
        duration = 21,
        startDate = today.minusDays(5),
        endDate = today.plusDays(15),
        currentWeek = 1,
        totalWeeks = 3,
        weeklyPlans = listOf(
            WeeklyPlan(
                weekNumber = 1,
                focus = "增加膳食纤维摄入",
                description = "第一周：开始增加膳食纤维摄入，逐步达到每日推荐量",
                targets = WeeklyTargets(fiber = 20.0, vegetables = 4, fruits = 3),
                dailyTasks = listOf(
                    DailyTask(
                        id = "task1",
                        title = "早餐添加一份燕麦",
                        description = "燕麦是优质的膳食纤维来源",
                        type = TaskType.NUTRITION,
                        isRequired = true
                    ),
                    DailyTask(
                        id = "task2",
                        title = "每天食用至少3种蔬菜",
                        description = "多样化的蔬菜摄入有助于获取不同类型的膳食纤维",
                        type = TaskType.NUTRITION,
                        isRequired = true
                    ),
                    DailyTask(
                        id = "task3",
                        title = "记录膳食纤维摄入量",
                        description = "使用应用记录今天摄入的膳食纤维总量",
                        type = TaskType.RECORDING,
                        isRequired = false
                    )
                ),
                successCriteria = listOf(
                    "每日膳食纤维摄入量达到15-20克",
                    "完成80%以上的必需任务"
                )
            )
        ),
        progress = 0.25f,
        milestones = listOf(
            Milestone(
                id = "milestone1",
                title = "第一周完成",
                description = "成功完成第一周的膳食纤维改善计划",
                weekNumber = 1,
                rewardPoints = 50
            )
        )
    )
}

// 改善计划视图组件
@Composable
fun ImprovementPlanView(plan: com.example.nutrilog.features.recommendation.model.improvement.ImprovementPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = AppShapes.large
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 标题和进度
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = plan.title,
                        style = AppTypography.h2,
                        color = AppColors.OnSurface
                    )
                    
                    Text(
                            text = "第${plan.getDaysPassed()}天 / 共${plan.duration}天",
                            style = AppTypography.body1,
                            color = AppColors.OnSurfaceVariant
                        )
                }
                
                Text(
                    text = "${(plan.progress * 100).toInt()}%",
                    style = AppTypography.h2.copy(fontSize = 28.sp),
                    color = getProgressColor((plan.progress * 100).toInt())
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 进度条
            LinearProgressIndicator(
                progress = plan.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = getProgressColor((plan.progress * 100).toInt()),
                trackColor = AppColors.Background
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 今日任务 - 从当前周计划中获取
            val currentWeekPlan = plan.weeklyPlans.firstOrNull { it.weekNumber == plan.currentWeek }
            if (currentWeekPlan != null && currentWeekPlan.dailyTasks.isNotEmpty()) {
                Column {
                    Text(
                        text = "今日任务",
                        style = AppTypography.h2.copy(fontSize = 18.sp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    currentWeekPlan.dailyTasks.forEach { task ->
                        TaskItem(task = task)
                    }
                }
            }
            
            // 下一个里程碑
            val nextMilestone = plan.milestones.firstOrNull { !it.achieved }
            nextMilestone?.let {
                Spacer(modifier = Modifier.height(16.dp))
                NextMilestoneCard(milestone = it)
            }
        }
    }
}
