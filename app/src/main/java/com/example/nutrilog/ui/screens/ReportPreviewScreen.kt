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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 报告摘要
        ReportSummary(report)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 营养分析
        ReportNutritionAnalysis(report)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 趋势分析
        ReportTrendAnalysis(report)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 改进建议
        ReportRecommendations(report)
        
        Spacer(modifier = Modifier.height(16.dp))
        
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
            .height(280.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 应用图标
            Icon(
                Icons.Default.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 报告标题
            Text(
                text = "营养健康报告",
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.White
                )
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 平均评分
            Box(
                modifier = Modifier
                    .size(106.dp) // 包含边框的总大小
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp) // 边框宽度
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = report.averageScore.toInt().toString(),
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "平均分",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )
                    }
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
            Text(text = "这是一份${report.type.name}营养报告，包含了${report.startDate}至${report.endDate}期间的营养摄入分析。")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "报告包含以下内容:")
            val contentOptions = report.contentOptions
            if (contentOptions.includeNutritionSummary) {
                BulletPoint(text = "营养摄入摘要")
            }
            if (contentOptions.includeTrendAnalysis) {
                BulletPoint(text = "营养趋势分析")
            }
            if (contentOptions.includeMealPatterns) {
                BulletPoint(text = "饮食模式分析")
            }
            if (contentOptions.includeRecommendations) {
                BulletPoint(text = "改进建议")
            }
            if (contentOptions.includeHealthScore) {
                BulletPoint(text = "健康评分")
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
            Text(text = "1. 增加膳食纤维的摄入")
            Text(text = "2. 控制脂肪摄入量")
            Text(text = "3. 保持规律的饮食习惯")
            Text(text = "4. 增加蛋白质的摄入")
        }
    }
}

// 报告结尾
@Composable
private fun ReportFooter(report: com.example.nutrilog.ui.models.GeneratedReport) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "© 2026 NutriLog",
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
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
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
