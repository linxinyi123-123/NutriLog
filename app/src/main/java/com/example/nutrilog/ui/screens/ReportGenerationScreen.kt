package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrilog.ui.models.ReportType
import com.example.nutrilog.ui.viewmodels.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportGenerationScreen(
    navController: NavController,
    viewModel: ReportViewModel
) {
    val reportTypes = listOf(
        ReportType.DAILY,
        ReportType.WEEKLY,
        ReportType.MONTHLY,
        ReportType.CUSTOM
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("生成报告") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            // 报告类型选择
            SectionTitle("报告类型")
            ReportTypeSelector(
                selectedType = viewModel.selectedReportType.value,
                onTypeSelected = { viewModel.selectReportType(it) }
            )
            
            // 日期范围选择
            SectionTitle("日期范围")
            DateRangeSelector(viewModel = viewModel)
            
            // 报告内容选项
            SectionTitle("包含内容")
            ContentOptionsSelector(viewModel = viewModel)
            
            // 报告样式选项
            SectionTitle("报告样式")
            StyleOptionsSelector(viewModel = viewModel)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 生成按钮
            Button(
                onClick = {
                    val report = viewModel.generateReport()
                    navController.navigate("report_preview/${report.id}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                enabled = viewModel.canGenerate.value,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                if (viewModel.isGenerating.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "生成报告",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp)
                    )
                }
            }
        }
    }
}

// 辅助组件：章节标题
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}

// 辅助组件：报告类型选择器
@Composable
private fun ReportTypeSelector(
    selectedType: ReportType,
    onTypeSelected: (ReportType) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            reportTypes.forEach {type ->
                Button(
                    onClick = { onTypeSelected(type) },
                    modifier = Modifier.weight(1f).padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == type) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                ) {
                    Text(text = type.name)
                }
            }
        }
    }
}

// 辅助组件：日期范围选择器
@Composable
private fun DateRangeSelector(viewModel: ReportViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "开始日期: ${viewModel.startDate.value}")
            Button(onClick = { /* 实现日期选择器 */ }) {
                Text(text = "选择")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "结束日期: ${viewModel.endDate.value}")
            Button(onClick = { /* 实现日期选择器 */ }) {
                Text(text = "选择")
            }
        }
    }
}

// 辅助组件：内容选项选择器
@Composable
private fun ContentOptionsSelector(viewModel: ReportViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        val options = viewModel.contentOptions.value
        
        CheckboxRow(
            label = "营养摘要",
            checked = options.includeNutritionSummary,
            onCheckedChange = { 
                viewModel.updateContentOptions(
                    options.copy(includeNutritionSummary = it)
                ) 
            }
        )
        
        CheckboxRow(
            label = "趋势分析",
            checked = options.includeTrendAnalysis,
            onCheckedChange = { 
                viewModel.updateContentOptions(
                    options.copy(includeTrendAnalysis = it)
                ) 
            }
        )
        
        CheckboxRow(
            label = "饮食模式",
            checked = options.includeMealPatterns,
            onCheckedChange = { 
                viewModel.updateContentOptions(
                    options.copy(includeMealPatterns = it)
                ) 
            }
        )
        
        CheckboxRow(
            label = "改进建议",
            checked = options.includeRecommendations,
            onCheckedChange = { 
                viewModel.updateContentOptions(
                    options.copy(includeRecommendations = it)
                ) 
            }
        )
        
        CheckboxRow(
            label = "健康评分",
            checked = options.includeHealthScore,
            onCheckedChange = { 
                viewModel.updateContentOptions(
                    options.copy(includeHealthScore = it)
                ) 
            }
        )
    }
}

// 辅助组件：样式选项选择器
@Composable
private fun StyleOptionsSelector(viewModel: ReportViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        val options = viewModel.styleOptions.value
        
        CheckboxRow(
            label = "包含图表",
            checked = options.includeCharts,
            onCheckedChange = { 
                viewModel.updateStyleOptions(
                    options.copy(includeCharts = it)
                ) 
            }
        )
        
        CheckboxRow(
            label = "包含表格",
            checked = options.includeTables,
            onCheckedChange = { 
                viewModel.updateStyleOptions(
                    options.copy(includeTables = it)
                ) 
            }
        )
    }
}

// 辅助组件：复选框行
@Composable
private fun CheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

// 报告类型列表
private val reportTypes = listOf(
    ReportType.DAILY,
    ReportType.WEEKLY,
    ReportType.MONTHLY,
    ReportType.CUSTOM
)