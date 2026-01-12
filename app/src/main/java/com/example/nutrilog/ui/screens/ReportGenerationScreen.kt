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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrilog.ui.models.ContentOptions
import com.example.nutrilog.ui.models.StyleOptions
import com.example.nutrilog.ui.models.ReportType
import com.example.nutrilog.ui.viewmodels.ReportViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    
    // 使用collectAsState()观察StateFlow状态变化
    val selectedReportType by viewModel.selectedReportType.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val contentOptions by viewModel.contentOptions.collectAsState()
    val styleOptions by viewModel.styleOptions.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val canGenerate by viewModel.canGenerate.collectAsState()
    
    // 日期选择状态
    val showStartDatePicker = remember { mutableStateOf(false) }
    val showEndDatePicker = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    
    // 日期选择器状态
    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDate.atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
    )
    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = endDate.atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
    )
    
    // 日期格式化
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
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
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    reportTypes.forEach { type ->
                        Button(
                            onClick = { viewModel.selectReportType(type) },
                            modifier = Modifier.weight(1f).padding(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedReportType == type) {
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
            
            // 日期范围选择
            SectionTitle("日期范围")
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "开始日期: ${startDate.format(dateFormatter)}")
                    Button(
                        onClick = {
                            selectedDate.value = startDate
                            showStartDatePicker.value = true
                        }
                    ) {
                        Text(text = "选择")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "结束日期: ${endDate.format(dateFormatter)}")
                    Button(
                        onClick = {
                            selectedDate.value = endDate
                            showEndDatePicker.value = true
                        }
                    ) {
                        Text(text = "选择")
                    }
                }
            }
            
            // 开始日期选择器
            if (showStartDatePicker.value) {
                AlertDialog(
                    onDismissRequest = { showStartDatePicker.value = false },
                    title = { Text("选择开始日期") },
                    text = {
                        DatePicker(state = startDatePickerState)
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                startDatePickerState.selectedDateMillis?.let {
                                    val date = LocalDate.ofInstant(
                                        java.time.Instant.ofEpochMilli(it),
                                        java.time.ZoneOffset.UTC
                                    )
                                    viewModel.setStartDate(date)
                                }
                                showStartDatePicker.value = false
                            }
                        ) {
                            Text("确认")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showStartDatePicker.value = false }
                        ) {
                            Text("取消")
                        }
                    }
                )
            }
            
            // 结束日期选择器
            if (showEndDatePicker.value) {
                AlertDialog(
                    onDismissRequest = { showEndDatePicker.value = false },
                    title = { Text("选择结束日期") },
                    text = {
                        DatePicker(state = endDatePickerState)
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                endDatePickerState.selectedDateMillis?.let {
                                    val date = LocalDate.ofInstant(
                                        java.time.Instant.ofEpochMilli(it),
                                        java.time.ZoneOffset.UTC
                                    )
                                    viewModel.setEndDate(date)
                                }
                                showEndDatePicker.value = false
                            }
                        ) {
                            Text("确认")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showEndDatePicker.value = false }
                        ) {
                            Text("取消")
                        }
                    }
                )
            }
            
            // 报告内容选项
            SectionTitle("包含内容")
            Column(modifier = Modifier.padding(16.dp)) {
                CheckboxRow(
                    label = "营养摘要",
                    checked = contentOptions.includeNutritionSummary,
                    onCheckedChange = {
                        viewModel.updateContentOptions(
                            contentOptions.copy(includeNutritionSummary = it)
                        )
                    }
                )
                
                CheckboxRow(
                    label = "趋势分析",
                    checked = contentOptions.includeTrendAnalysis,
                    onCheckedChange = {
                        viewModel.updateContentOptions(
                            contentOptions.copy(includeTrendAnalysis = it)
                        )
                    }
                )
                
                CheckboxRow(
                    label = "饮食模式",
                    checked = contentOptions.includeMealPatterns,
                    onCheckedChange = {
                        viewModel.updateContentOptions(
                            contentOptions.copy(includeMealPatterns = it)
                        )
                    }
                )
                
                CheckboxRow(
                    label = "改进建议",
                    checked = contentOptions.includeRecommendations,
                    onCheckedChange = {
                        viewModel.updateContentOptions(
                            contentOptions.copy(includeRecommendations = it)
                        )
                    }
                )
                
                CheckboxRow(
                    label = "健康评分",
                    checked = contentOptions.includeHealthScore,
                    onCheckedChange = {
                        viewModel.updateContentOptions(
                            contentOptions.copy(includeHealthScore = it)
                        )
                    }
                )
            }
            
            // 报告样式选项
            SectionTitle("报告样式")
            Column(modifier = Modifier.padding(16.dp)) {
                CheckboxRow(
                    label = "包含图表",
                    checked = styleOptions.includeCharts,
                    onCheckedChange = {
                        viewModel.updateStyleOptions(
                            styleOptions.copy(includeCharts = it)
                        )
                    }
                )
                
                CheckboxRow(
                    label = "包含表格",
                    checked = styleOptions.includeTables,
                    onCheckedChange = {
                        viewModel.updateStyleOptions(
                            styleOptions.copy(includeTables = it)
                        )
                    }
                )
            }
            
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
                enabled = canGenerate,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                if (isGenerating) {
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

// 完整报告生成屏幕预览
@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    device = "id:pixel_5",
    name = "Report Generation Screen"
)
@Composable
private fun ReportGenerationScreenPreview() {
    MaterialTheme {
        ReportGenerationScreenContent()
    }
}

// 报告生成屏幕内容组件，不依赖ViewModel和NavController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportGenerationScreenContent() {
    var selectedType by remember { mutableStateOf(ReportType.WEEKLY) }
    var startDate by remember { mutableStateOf("2026-01-01") }
    var endDate by remember { mutableStateOf("2026-01-07") }
    var contentOptions by remember { mutableStateOf(ContentOptions()) }
    var styleOptions by remember { mutableStateOf(StyleOptions()) }
    var isGenerating by remember { mutableStateOf(false) }
    var canGenerate by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("生成报告") },
                navigationIcon = {
                    IconButton(onClick = {}) {
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
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    reportTypes.forEach { type ->
                        Button(
                            onClick = { selectedType = type },
                            modifier = Modifier.weight(1f).padding(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == type) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer
                                }
                            )
                        ) {
                            Text(text = when (type) {
                                ReportType.DAILY -> "日报"
                                ReportType.WEEKLY -> "周报"
                                ReportType.MONTHLY -> "月报"
                                ReportType.CUSTOM -> "自定"
                                else -> type.name
                            })
                        }
                    }
                }
            }
            
            // 日期范围选择
            SectionTitle("日期范围")
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "开始日期: $startDate")
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
                    Text(text = "结束日期: $endDate")
                    Button(onClick = { /* 实现日期选择器 */ }) {
                        Text(text = "选择")
                    }
                }
            }
            
            // 报告内容选项
            SectionTitle("包含内容")
            Column(modifier = Modifier.padding(16.dp)) {
                CheckboxRow(
                    label = "营养摘要",
                    checked = contentOptions.includeNutritionSummary,
                    onCheckedChange = {
                        contentOptions = contentOptions.copy(includeNutritionSummary = it)
                    }
                )
                
                CheckboxRow(
                    label = "趋势分析",
                    checked = contentOptions.includeTrendAnalysis,
                    onCheckedChange = {
                        contentOptions = contentOptions.copy(includeTrendAnalysis = it)
                    }
                )
                
                CheckboxRow(
                    label = "饮食模式",
                    checked = contentOptions.includeMealPatterns,
                    onCheckedChange = {
                        contentOptions = contentOptions.copy(includeMealPatterns = it)
                    }
                )
                
                CheckboxRow(
                    label = "改进建议",
                    checked = contentOptions.includeRecommendations,
                    onCheckedChange = {
                        contentOptions = contentOptions.copy(includeRecommendations = it)
                    }
                )
                
                CheckboxRow(
                    label = "健康评分",
                    checked = contentOptions.includeHealthScore,
                    onCheckedChange = {
                        contentOptions = contentOptions.copy(includeHealthScore = it)
                    }
                )
            }
            
            // 报告样式选项
            SectionTitle("报告样式")
            Column(modifier = Modifier.padding(16.dp)) {
                CheckboxRow(
                    label = "包含图表",
                    checked = styleOptions.includeCharts,
                    onCheckedChange = {
                        styleOptions = styleOptions.copy(includeCharts = it)
                    }
                )
                
                CheckboxRow(
                    label = "包含表格",
                    checked = styleOptions.includeTables,
                    onCheckedChange = {
                        styleOptions = styleOptions.copy(includeTables = it)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 生成按钮
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                enabled = canGenerate,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                if (isGenerating) {
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

// 章节标题预览
@Preview(
    showBackground = true,
    name = "Section Title"
)
@Composable
private fun SectionTitlePreview() {
    MaterialTheme {
        SectionTitle("预览章节标题")
    }
}

// 报告类型选择器预览
@Preview(
    showBackground = true,
    name = "Report Type Selector"
)
@Composable
private fun ReportTypeSelectorPreview() {
    MaterialTheme {
        var selectedType by remember { mutableStateOf(ReportType.WEEKLY) }
        
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                reportTypes.forEach { type ->
                    Button(
                        onClick = { selectedType = type },
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
}

// 日期范围选择器预览
@Preview(
    showBackground = true,
    name = "Date Range Selector"
)
@Composable
private fun DateRangeSelectorPreview() {
    MaterialTheme {
        var startDate by remember { mutableStateOf("2026-01-01") }
        var endDate by remember { mutableStateOf("2026-01-07") }
        
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "开始日期: $startDate")
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
                Text(text = "结束日期: $endDate")
                Button(onClick = { /* 实现日期选择器 */ }) {
                    Text(text = "选择")
                }
            }
        }
    }
}

// 内容选项选择器预览
@Preview(
    showBackground = true,
    name = "Content Options Selector"
)
@Composable
private fun ContentOptionsSelectorPreview() {
    MaterialTheme {
        var contentOptions by remember { mutableStateOf(ContentOptions()) }
        
        Column(modifier = Modifier.padding(16.dp)) {
            CheckboxRow(
                label = "营养摘要",
                checked = contentOptions.includeNutritionSummary,
                onCheckedChange = {
                    contentOptions = contentOptions.copy(includeNutritionSummary = it)
                }
            )
            
            CheckboxRow(
                label = "趋势分析",
                checked = contentOptions.includeTrendAnalysis,
                onCheckedChange = {
                    contentOptions = contentOptions.copy(includeTrendAnalysis = it)
                }
            )
            
            CheckboxRow(
                label = "饮食模式",
                checked = contentOptions.includeMealPatterns,
                onCheckedChange = {
                    contentOptions = contentOptions.copy(includeMealPatterns = it)
                }
            )
            
            CheckboxRow(
                label = "改进建议",
                checked = contentOptions.includeRecommendations,
                onCheckedChange = {
                    contentOptions = contentOptions.copy(includeRecommendations = it)
                }
            )
            
            CheckboxRow(
                label = "健康评分",
                checked = contentOptions.includeHealthScore,
                onCheckedChange = {
                    contentOptions = contentOptions.copy(includeHealthScore = it)
                }
            )
        }
    }
}

// 样式选项选择器预览
@Preview(
    showBackground = true,
    name = "Style Options Selector"
)
@Composable
private fun StyleOptionsSelectorPreview() {
    MaterialTheme {
        var styleOptions by remember { mutableStateOf(StyleOptions()) }
        
        Column(modifier = Modifier.padding(16.dp)) {
            CheckboxRow(
                label = "包含图表",
                checked = styleOptions.includeCharts,
                onCheckedChange = {
                    styleOptions = styleOptions.copy(includeCharts = it)
                }
            )
            
            CheckboxRow(
                label = "包含表格",
                checked = styleOptions.includeTables,
                onCheckedChange = {
                    styleOptions = styleOptions.copy(includeTables = it)
                }
            )
        }
    }
}

// 复选框行预览
@Preview(
    showBackground = true,
    name = "Checkbox Row"
)
@Composable
private fun CheckboxRowPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CheckboxRow(
                label = "预览复选框",
                checked = true,
                onCheckedChange = {}
            )
            CheckboxRow(
                label = "未选中的复选框",
                checked = false,
                onCheckedChange = {}
            )
        }
    }
}