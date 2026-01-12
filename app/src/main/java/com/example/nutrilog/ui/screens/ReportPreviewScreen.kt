package com.example.nutrilog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrilog.ui.models.GeneratedReport
import com.example.nutrilog.ui.models.ContentOptions
import com.example.nutrilog.ui.models.StyleOptions
import com.example.nutrilog.ui.models.ReportType
import com.example.nutrilog.ui.models.ShareFormat
import com.example.nutrilog.ui.viewmodels.ReportPreviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPreviewScreen(
    reportId: String,
    navController: NavController,
    viewModel: ReportPreviewViewModel = remember { com.example.nutrilog.di.AppModule.provideReportPreviewViewModel() }
) {
    // 加载报告数据
    LaunchedEffect(reportId) {
        viewModel.loadReport(reportId)
    }
    
    val report by viewModel.report.collectAsState()
    
    // 调试用，检查报告状态
    LaunchedEffect(report) {
        println("Report state changed: $report")
    }
    
    // 分享对话框状态
    val showShareDialog = remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            ReportPreviewTopBar(
                report = report,
                onShare = { showShareDialog.value = true },
                onExport = { viewModel.exportReport() },
                onBack = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.saveReport() },
                icon = { Icon(Icons.Filled.Save, "保存") },
                text = { Text("保存报告") }
            )
        }
    ) {
        if (viewModel.isLoading.value) {
            ReportLoadingView()
        } else if (viewModel.errorMessage.value != null) {
            ReportErrorView(
                message = viewModel.errorMessage.value!!,
                onRetry = { viewModel.loadReport(reportId) }
            )
        } else if (report == null) {
            ReportEmptyView()
        } else {
            ReportContentView(
                report = report!!,
                modifier = Modifier.padding(it)
            )
        }
    }
    
    // 分享报告对话框
    ShareReportDialog(
        isVisible = showShareDialog.value,
        onDismiss = { showShareDialog.value = false },
        onShare = { format ->
            // 实现分享逻辑
            viewModel.shareReport(format)
        }
    )
}

// 报告预览顶部栏
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportPreviewTopBar(
    report: com.example.nutrilog.ui.models.GeneratedReport?,
    onShare: () -> Unit,
    onExport: () -> Unit,
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text("报告预览") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "返回")
            }
        },
        actions = {
            IconButton(onClick = onShare) {
                Icon(Icons.Filled.Share, "分享")
            }
            IconButton(onClick = onExport) {
                Icon(Icons.Filled.Download, "导出")
            }
        }
    )
}

// 报告内容视图
@Composable
private fun ReportContentView(
    report: com.example.nutrilog.ui.models.GeneratedReport,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 报告封面
        ReportCover(report)
        
        // 报告摘要
        ReportSummary(report)
        
        // 营养分析
        ReportNutritionAnalysis(report)
        
        // 趋势分析
        ReportTrendAnalysis(report)
        
        // 改进建议
        ReportRecommendations(report)
        
        // 报告结尾
        ReportFooter(report)
    }
}

// 报告封面
@Composable
private fun ReportCover(report: com.example.nutrilog.ui.models.GeneratedReport) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(0f, 260f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // 应用图标
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 报告标题
            Text(
                text = "营养健康报告",
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 报告副标题
            Text(
                text = "${report.startDate} 至 ${report.endDate}",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // 平均评分
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White,
                                Color.White.copy(alpha = 0.9f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = report.averageScore.toInt().toString(),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "平均分",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 生成时间
            Text(
                text = "生成于: ${report.generatedTime}",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            )
        }
    }
}

// 报告摘要
@Composable
private fun ReportSummary(report: com.example.nutrilog.ui.models.GeneratedReport) {
    SectionCard(title = "报告摘要") {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "这是一份${report.type.name}营养报告，包含了${report.startDate}至${report.endDate}期间的营养摄入分析。",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "报告包含以下内容:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val contentOptions = report.contentOptions
            val contentItems = listOfNotNull(
                contentOptions.includeNutritionSummary to "营养摄入摘要",
                contentOptions.includeTrendAnalysis to "营养趋势分析",
                contentOptions.includeMealPatterns to "饮食模式分析",
                contentOptions.includeRecommendations to "改进建议",
                contentOptions.includeHealthScore to "健康评分"
            ).filter { it.first }.map { it.second }
            
            contentItems.forEachIndexed { index, item ->
                BulletPoint(text = item)
                if (index < contentItems.size - 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

// 营养分析
@Composable
private fun ReportNutritionAnalysis(report: com.example.nutrilog.ui.models.GeneratedReport) {
    SectionCard(title = "营养分析") {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "营养分析内容将在此显示。")
            // 实际应用中，这里应该显示具体的营养数据和图表
        }
    }
}

// 趋势分析
@Composable
private fun ReportTrendAnalysis(report: com.example.nutrilog.ui.models.GeneratedReport) {
    SectionCard(title = "趋势分析") {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "趋势分析内容将在此显示。")
            // 实际应用中，这里应该显示趋势图表和分析
        }
    }
}

// 改进建议
@Composable
private fun ReportRecommendations(report: com.example.nutrilog.ui.models.GeneratedReport) {
    SectionCard(title = "改进建议") {
        Column(modifier = Modifier.padding(16.dp)) {
            val recommendations = listOf(
                "增加膳食纤维的摄入",
                "控制脂肪摄入量",
                "保持规律的饮食习惯",
                "增加蛋白质的摄入"
            )
            
            recommendations.forEachIndexed { index, recommendation ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .clip(CircleShape)
                                .align(Alignment.Top)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// 报告结尾
@Composable
private fun ReportFooter(report: com.example.nutrilog.ui.models.GeneratedReport) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "© 2026 NutriLog",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "专注于您的营养健康",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 加载视图
@Composable
private fun ReportLoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

// 错误视图
@Composable
private fun ReportErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = "重试")
        }
    }
}

// 空视图
@Composable
private fun ReportEmptyView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "报告不存在")
    }
}

// 章节卡片
@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            hoveredElevation = 2.dp,
            focusedElevation = 2.dp
        )
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
            content()
        }
    }
}

// 项目符号
@Composable
private fun BulletPoint(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(MaterialTheme.colorScheme.primary)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

// 分享报告对话框
@Composable
private fun ShareReportDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onShare: (ShareFormat) -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("分享报告") },
            text = {
                Column {
                    Text("选择分享格式:")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ShareFormat.values().forEach { format ->
                        ShareFormatItem(
                            format = format,
                            onClick = {
                                onShare(format)
                                onDismiss()
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
            }
        )
    }
}

// 分享格式选项
@Composable
private fun ShareFormatItem(
    format: ShareFormat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getFormatIcon(format),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = format.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = format.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// 获取格式对应的图标
private fun getFormatIcon(format: ShareFormat) = when (format) {
    ShareFormat.PDF -> Icons.Filled.PictureAsPdf
    ShareFormat.IMAGE -> Icons.Filled.Image
    ShareFormat.TEXT -> Icons.Filled.Description
}

// 模拟报告数据
private fun mockReport() = GeneratedReport(
    id = "1",
    type = ReportType.WEEKLY,
    startDate = java.time.LocalDate.of(2026, 1, 1),
    endDate = java.time.LocalDate.of(2026, 1, 7),
    contentOptions = ContentOptions(
        includeNutritionSummary = true,
        includeTrendAnalysis = true,
        includeMealPatterns = true,
        includeRecommendations = true,
        includeHealthScore = true
    ),
    styleOptions = StyleOptions()
)

// 预览整个报告预览屏幕
@Preview(
    showBackground = true,
    device = "id:pixel_5",
    name = "Report Preview Screen"
)
@Composable
private fun ReportPreviewScreenPreview() {
    MaterialTheme {
        ReportContentPreview()
    }
}

// 报告内容预览，不依赖ViewModel和NavController
@Composable
private fun ReportContentPreview() {
    val report = mockReport()
    
    Scaffold(
        topBar = {
            ReportPreviewTopBar(
                report = report,
                onShare = {},
                onExport = {},
                onBack = {}
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {},
                icon = { Icon(Icons.Filled.Save, "保存") },
                text = { Text("保存报告") }
            )
        }
    ) {
        ReportContentView(
            report = report,
            modifier = Modifier.padding(it)
        )
    }
}

// 预览报告封面
@Preview(
    showBackground = true,
    name = "Report Cover"
)
@Composable
private fun ReportCoverPreview() {
    MaterialTheme {
        ReportCover(report = mockReport())
    }
}

// 预览报告摘要
@Preview(
    showBackground = true,
    name = "Report Summary"
)
@Composable
private fun ReportSummaryPreview() {
    MaterialTheme {
        ReportSummary(report = mockReport())
    }
}

// 预览营养分析
@Preview(
    showBackground = true,
    name = "Report Nutrition Analysis"
)
@Composable
private fun ReportNutritionAnalysisPreview() {
    MaterialTheme {
        ReportNutritionAnalysis(report = mockReport())
    }
}

// 预览趋势分析
@Preview(
    showBackground = true,
    name = "Report Trend Analysis"
)
@Composable
private fun ReportTrendAnalysisPreview() {
    MaterialTheme {
        ReportTrendAnalysis(report = mockReport())
    }
}

// 预览改进建议
@Preview(
    showBackground = true,
    name = "Report Recommendations"
)
@Composable
private fun ReportRecommendationsPreview() {
    MaterialTheme {
        ReportRecommendations(report = mockReport())
    }
}

// 预览报告结尾
@Preview(
    showBackground = true,
    name = "Report Footer"
)
@Composable
private fun ReportFooterPreview() {
    MaterialTheme {
        ReportFooter(report = mockReport())
    }
}

// 预览章节卡片
@Preview(
    showBackground = true,
    name = "Section Card"
)
@Composable
private fun SectionCardPreview() {
    MaterialTheme {
        SectionCard(title = "预览章节") {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "这是一个章节卡片的预览内容。")
                Text(text = "您可以在这里看到卡片的样式和布局。")
            }
        }
    }
}

// 预览项目符号
@Preview(
    showBackground = true,
    name = "Bullet Point"
)
@Composable
private fun BulletPointPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            BulletPoint(text = "项目符号1")
            BulletPoint(text = "项目符号2")
            BulletPoint(text = "项目符号3")
        }
    }
}

// 预览分享对话框
@Preview(
    showBackground = true,
    name = "Share Dialog"
)
@Composable
private fun ShareReportDialogPreview() {
    MaterialTheme {
        ShareReportDialog(
            isVisible = true,
            onDismiss = {},
            onShare = {}
        )
    }
}

// 预览加载视图
@Preview(
    showBackground = true,
    name = "Loading View"
)
@Composable
private fun ReportLoadingViewPreview() {
    MaterialTheme {
        ReportLoadingView()
    }
}

// 预览错误视图
@Preview(
    showBackground = true,
    name = "Error View"
)
@Composable
private fun ReportErrorViewPreview() {
    MaterialTheme {
        ReportErrorView(
            message = "加载失败，请重试。",
            onRetry = {}
        )
    }
}

// 预览空视图
@Preview(
    showBackground = true,
    name = "Empty View"
)
@Composable
private fun ReportEmptyViewPreview() {
    MaterialTheme {
        ReportEmptyView()
    }
}
